import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IWare } from 'app/entities/ware/ware.model';
import { WareService } from 'app/entities/ware/service/ware.service';
import { IProductReturn } from 'app/entities/product-return/product-return.model';
import { ProductReturnService } from 'app/entities/product-return/service/product-return.service';
import { IReturnedStock } from '../returned-stock.model';
import { ReturnedStockService } from '../service/returned-stock.service';
import { ReturnedStockFormService } from './returned-stock-form.service';

import { ReturnedStockUpdateComponent } from './returned-stock-update.component';

describe('ReturnedStock Management Update Component', () => {
  let comp: ReturnedStockUpdateComponent;
  let fixture: ComponentFixture<ReturnedStockUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let returnedStockFormService: ReturnedStockFormService;
  let returnedStockService: ReturnedStockService;
  let wareService: WareService;
  let productReturnService: ProductReturnService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ReturnedStockUpdateComponent],
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
      .overrideTemplate(ReturnedStockUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ReturnedStockUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    returnedStockFormService = TestBed.inject(ReturnedStockFormService);
    returnedStockService = TestBed.inject(ReturnedStockService);
    wareService = TestBed.inject(WareService);
    productReturnService = TestBed.inject(ProductReturnService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call returnedWare query and add missing value', () => {
      const returnedStock: IReturnedStock = { id: 456 };
      const returnedWare: IWare = { id: 21982 };
      returnedStock.returnedWare = returnedWare;

      const returnedWareCollection: IWare[] = [{ id: 28917 }];
      jest.spyOn(wareService, 'query').mockReturnValue(of(new HttpResponse({ body: returnedWareCollection })));
      const expectedCollection: IWare[] = [returnedWare, ...returnedWareCollection];
      jest.spyOn(wareService, 'addWareToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ returnedStock });
      comp.ngOnInit();

      expect(wareService.query).toHaveBeenCalled();
      expect(wareService.addWareToCollectionIfMissing).toHaveBeenCalledWith(returnedWareCollection, returnedWare);
      expect(comp.returnedWaresCollection).toEqual(expectedCollection);
    });

    it('Should call ProductReturn query and add missing value', () => {
      const returnedStock: IReturnedStock = { id: 456 };
      const productReturn: IProductReturn = { id: 22099 };
      returnedStock.productReturn = productReturn;

      const productReturnCollection: IProductReturn[] = [{ id: 13978 }];
      jest.spyOn(productReturnService, 'query').mockReturnValue(of(new HttpResponse({ body: productReturnCollection })));
      const additionalProductReturns = [productReturn];
      const expectedCollection: IProductReturn[] = [...additionalProductReturns, ...productReturnCollection];
      jest.spyOn(productReturnService, 'addProductReturnToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ returnedStock });
      comp.ngOnInit();

      expect(productReturnService.query).toHaveBeenCalled();
      expect(productReturnService.addProductReturnToCollectionIfMissing).toHaveBeenCalledWith(
        productReturnCollection,
        ...additionalProductReturns.map(expect.objectContaining),
      );
      expect(comp.productReturnsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const returnedStock: IReturnedStock = { id: 456 };
      const returnedWare: IWare = { id: 8905 };
      returnedStock.returnedWare = returnedWare;
      const productReturn: IProductReturn = { id: 30223 };
      returnedStock.productReturn = productReturn;

      activatedRoute.data = of({ returnedStock });
      comp.ngOnInit();

      expect(comp.returnedWaresCollection).toContain(returnedWare);
      expect(comp.productReturnsSharedCollection).toContain(productReturn);
      expect(comp.returnedStock).toEqual(returnedStock);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IReturnedStock>>();
      const returnedStock = { id: 123 };
      jest.spyOn(returnedStockFormService, 'getReturnedStock').mockReturnValue(returnedStock);
      jest.spyOn(returnedStockService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ returnedStock });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: returnedStock }));
      saveSubject.complete();

      // THEN
      expect(returnedStockFormService.getReturnedStock).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(returnedStockService.update).toHaveBeenCalledWith(expect.objectContaining(returnedStock));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IReturnedStock>>();
      const returnedStock = { id: 123 };
      jest.spyOn(returnedStockFormService, 'getReturnedStock').mockReturnValue({ id: null });
      jest.spyOn(returnedStockService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ returnedStock: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: returnedStock }));
      saveSubject.complete();

      // THEN
      expect(returnedStockFormService.getReturnedStock).toHaveBeenCalled();
      expect(returnedStockService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IReturnedStock>>();
      const returnedStock = { id: 123 };
      jest.spyOn(returnedStockService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ returnedStock });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(returnedStockService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareWare', () => {
      it('Should forward to wareService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(wareService, 'compareWare');
        comp.compareWare(entity, entity2);
        expect(wareService.compareWare).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareProductReturn', () => {
      it('Should forward to productReturnService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(productReturnService, 'compareProductReturn');
        comp.compareProductReturn(entity, entity2);
        expect(productReturnService.compareProductReturn).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
