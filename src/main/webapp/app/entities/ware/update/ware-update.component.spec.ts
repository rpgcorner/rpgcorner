import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { ICategory } from 'app/entities/category/category.model';
import { CategoryService } from 'app/entities/category/service/category.service';
import { WareService } from '../service/ware.service';
import { IWare } from '../ware.model';
import { WareFormService } from './ware-form.service';

import { WareUpdateComponent } from './ware-update.component';

describe('Ware Management Update Component', () => {
  let comp: WareUpdateComponent;
  let fixture: ComponentFixture<WareUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let wareFormService: WareFormService;
  let wareService: WareService;
  let categoryService: CategoryService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [WareUpdateComponent],
      providers: [
        provideHttpClient(),
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(WareUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(WareUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    wareFormService = TestBed.inject(WareFormService);
    wareService = TestBed.inject(WareService);
    categoryService = TestBed.inject(CategoryService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Category query and add missing value', () => {
      const ware: IWare = { id: 456 };
      const mainCategory: ICategory = { id: 25924 };
      ware.mainCategory = mainCategory;
      const subCategory: ICategory = { id: 22185 };
      ware.subCategory = subCategory;

      const categoryCollection: ICategory[] = [{ id: 23100 }];
      jest.spyOn(categoryService, 'query').mockReturnValue(of(new HttpResponse({ body: categoryCollection })));
      const additionalCategories = [mainCategory, subCategory];
      const expectedCollection: ICategory[] = [...additionalCategories, ...categoryCollection];
      jest.spyOn(categoryService, 'addCategoryToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ ware });
      comp.ngOnInit();

      expect(categoryService.query).toHaveBeenCalled();
      expect(categoryService.addCategoryToCollectionIfMissing).toHaveBeenCalledWith(
        categoryCollection,
        ...additionalCategories.map(expect.objectContaining),
      );
      expect(comp.categoriesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const ware: IWare = { id: 456 };
      const mainCategory: ICategory = { id: 5251 };
      ware.mainCategory = mainCategory;
      const subCategory: ICategory = { id: 6741 };
      ware.subCategory = subCategory;

      activatedRoute.data = of({ ware });
      comp.ngOnInit();

      expect(comp.categoriesSharedCollection).toContain(mainCategory);
      expect(comp.categoriesSharedCollection).toContain(subCategory);
      expect(comp.ware).toEqual(ware);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IWare>>();
      const ware = { id: 123 };
      jest.spyOn(wareFormService, 'getWare').mockReturnValue(ware);
      jest.spyOn(wareService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ ware });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: ware }));
      saveSubject.complete();

      // THEN
      expect(wareFormService.getWare).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(wareService.update).toHaveBeenCalledWith(expect.objectContaining(ware));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IWare>>();
      const ware = { id: 123 };
      jest.spyOn(wareFormService, 'getWare').mockReturnValue({ id: null });
      jest.spyOn(wareService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ ware: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: ware }));
      saveSubject.complete();

      // THEN
      expect(wareFormService.getWare).toHaveBeenCalled();
      expect(wareService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IWare>>();
      const ware = { id: 123 };
      jest.spyOn(wareService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ ware });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(wareService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareCategory', () => {
      it('Should forward to categoryService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(categoryService, 'compareCategory');
        comp.compareCategory(entity, entity2);
        expect(categoryService.compareCategory).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
