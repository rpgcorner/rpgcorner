import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IWare } from 'app/entities/ware/ware.model';
import { WareService } from 'app/entities/ware/service/ware.service';
import { IDispose } from 'app/entities/dispose/dispose.model';
import { DisposeService } from 'app/entities/dispose/service/dispose.service';
import { IDisposedStock } from '../disposed-stock.model';
import { DisposedStockService } from '../service/disposed-stock.service';
import { DisposedStockFormService } from './disposed-stock-form.service';

import { DisposedStockUpdateComponent } from './disposed-stock-update.component';

describe('DisposedStock Management Update Component', () => {
  let comp: DisposedStockUpdateComponent;
  let fixture: ComponentFixture<DisposedStockUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let disposedStockFormService: DisposedStockFormService;
  let disposedStockService: DisposedStockService;
  let wareService: WareService;
  let disposeService: DisposeService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [DisposedStockUpdateComponent],
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
      .overrideTemplate(DisposedStockUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(DisposedStockUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    disposedStockFormService = TestBed.inject(DisposedStockFormService);
    disposedStockService = TestBed.inject(DisposedStockService);
    wareService = TestBed.inject(WareService);
    disposeService = TestBed.inject(DisposeService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call disposedWare query and add missing value', () => {
      const disposedStock: IDisposedStock = { id: 456 };
      const disposedWare: IWare = { id: 12099 };
      disposedStock.disposedWare = disposedWare;

      const disposedWareCollection: IWare[] = [{ id: 24830 }];
      jest.spyOn(wareService, 'query').mockReturnValue(of(new HttpResponse({ body: disposedWareCollection })));
      const expectedCollection: IWare[] = [disposedWare, ...disposedWareCollection];
      jest.spyOn(wareService, 'addWareToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ disposedStock });
      comp.ngOnInit();

      expect(wareService.query).toHaveBeenCalled();
      expect(wareService.addWareToCollectionIfMissing).toHaveBeenCalledWith(disposedWareCollection, disposedWare);
      expect(comp.disposedWaresCollection).toEqual(expectedCollection);
    });

    it('Should call Dispose query and add missing value', () => {
      const disposedStock: IDisposedStock = { id: 456 };
      const dispose: IDispose = { id: 1971 };
      disposedStock.dispose = dispose;

      const disposeCollection: IDispose[] = [{ id: 27509 }];
      jest.spyOn(disposeService, 'query').mockReturnValue(of(new HttpResponse({ body: disposeCollection })));
      const additionalDisposes = [dispose];
      const expectedCollection: IDispose[] = [...additionalDisposes, ...disposeCollection];
      jest.spyOn(disposeService, 'addDisposeToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ disposedStock });
      comp.ngOnInit();

      expect(disposeService.query).toHaveBeenCalled();
      expect(disposeService.addDisposeToCollectionIfMissing).toHaveBeenCalledWith(
        disposeCollection,
        ...additionalDisposes.map(expect.objectContaining),
      );
      expect(comp.disposesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const disposedStock: IDisposedStock = { id: 456 };
      const disposedWare: IWare = { id: 24732 };
      disposedStock.disposedWare = disposedWare;
      const dispose: IDispose = { id: 29637 };
      disposedStock.dispose = dispose;

      activatedRoute.data = of({ disposedStock });
      comp.ngOnInit();

      expect(comp.disposedWaresCollection).toContain(disposedWare);
      expect(comp.disposesSharedCollection).toContain(dispose);
      expect(comp.disposedStock).toEqual(disposedStock);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDisposedStock>>();
      const disposedStock = { id: 123 };
      jest.spyOn(disposedStockFormService, 'getDisposedStock').mockReturnValue(disposedStock);
      jest.spyOn(disposedStockService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ disposedStock });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: disposedStock }));
      saveSubject.complete();

      // THEN
      expect(disposedStockFormService.getDisposedStock).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(disposedStockService.update).toHaveBeenCalledWith(expect.objectContaining(disposedStock));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDisposedStock>>();
      const disposedStock = { id: 123 };
      jest.spyOn(disposedStockFormService, 'getDisposedStock').mockReturnValue({ id: null });
      jest.spyOn(disposedStockService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ disposedStock: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: disposedStock }));
      saveSubject.complete();

      // THEN
      expect(disposedStockFormService.getDisposedStock).toHaveBeenCalled();
      expect(disposedStockService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDisposedStock>>();
      const disposedStock = { id: 123 };
      jest.spyOn(disposedStockService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ disposedStock });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(disposedStockService.update).toHaveBeenCalled();
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

    describe('compareDispose', () => {
      it('Should forward to disposeService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(disposeService, 'compareDispose');
        comp.compareDispose(entity, entity2);
        expect(disposeService.compareDispose).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
