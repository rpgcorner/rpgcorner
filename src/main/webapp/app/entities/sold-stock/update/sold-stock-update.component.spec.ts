import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IWare } from 'app/entities/ware/ware.model';
import { WareService } from 'app/entities/ware/service/ware.service';
import { ISale } from 'app/entities/sale/sale.model';
import { SaleService } from 'app/entities/sale/service/sale.service';
import { ISoldStock } from '../sold-stock.model';
import { SoldStockService } from '../service/sold-stock.service';
import { SoldStockFormService } from './sold-stock-form.service';

import { SoldStockUpdateComponent } from './sold-stock-update.component';

describe('SoldStock Management Update Component', () => {
  let comp: SoldStockUpdateComponent;
  let fixture: ComponentFixture<SoldStockUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let soldStockFormService: SoldStockFormService;
  let soldStockService: SoldStockService;
  let wareService: WareService;
  let saleService: SaleService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [SoldStockUpdateComponent],
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
      .overrideTemplate(SoldStockUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(SoldStockUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    soldStockFormService = TestBed.inject(SoldStockFormService);
    soldStockService = TestBed.inject(SoldStockService);
    wareService = TestBed.inject(WareService);
    saleService = TestBed.inject(SaleService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call soldWare query and add missing value', () => {
      const soldStock: ISoldStock = { id: 456 };
      const soldWare: IWare = { id: 25989 };
      soldStock.soldWare = soldWare;

      const soldWareCollection: IWare[] = [{ id: 5489 }];
      jest.spyOn(wareService, 'query').mockReturnValue(of(new HttpResponse({ body: soldWareCollection })));
      const expectedCollection: IWare[] = [soldWare, ...soldWareCollection];
      jest.spyOn(wareService, 'addWareToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ soldStock });
      comp.ngOnInit();

      expect(wareService.query).toHaveBeenCalled();
      expect(wareService.addWareToCollectionIfMissing).toHaveBeenCalledWith(soldWareCollection, soldWare);
      expect(comp.soldWaresCollection).toEqual(expectedCollection);
    });

    it('Should call Sale query and add missing value', () => {
      const soldStock: ISoldStock = { id: 456 };
      const sale: ISale = { id: 5597 };
      soldStock.sale = sale;

      const saleCollection: ISale[] = [{ id: 25930 }];
      jest.spyOn(saleService, 'query').mockReturnValue(of(new HttpResponse({ body: saleCollection })));
      const additionalSales = [sale];
      const expectedCollection: ISale[] = [...additionalSales, ...saleCollection];
      jest.spyOn(saleService, 'addSaleToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ soldStock });
      comp.ngOnInit();

      expect(saleService.query).toHaveBeenCalled();
      expect(saleService.addSaleToCollectionIfMissing).toHaveBeenCalledWith(
        saleCollection,
        ...additionalSales.map(expect.objectContaining),
      );
      expect(comp.salesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const soldStock: ISoldStock = { id: 456 };
      const soldWare: IWare = { id: 20494 };
      soldStock.soldWare = soldWare;
      const sale: ISale = { id: 32484 };
      soldStock.sale = sale;

      activatedRoute.data = of({ soldStock });
      comp.ngOnInit();

      expect(comp.soldWaresCollection).toContain(soldWare);
      expect(comp.salesSharedCollection).toContain(sale);
      expect(comp.soldStock).toEqual(soldStock);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISoldStock>>();
      const soldStock = { id: 123 };
      jest.spyOn(soldStockFormService, 'getSoldStock').mockReturnValue(soldStock);
      jest.spyOn(soldStockService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ soldStock });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: soldStock }));
      saveSubject.complete();

      // THEN
      expect(soldStockFormService.getSoldStock).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(soldStockService.update).toHaveBeenCalledWith(expect.objectContaining(soldStock));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISoldStock>>();
      const soldStock = { id: 123 };
      jest.spyOn(soldStockFormService, 'getSoldStock').mockReturnValue({ id: null });
      jest.spyOn(soldStockService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ soldStock: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: soldStock }));
      saveSubject.complete();

      // THEN
      expect(soldStockFormService.getSoldStock).toHaveBeenCalled();
      expect(soldStockService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISoldStock>>();
      const soldStock = { id: 123 };
      jest.spyOn(soldStockService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ soldStock });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(soldStockService.update).toHaveBeenCalled();
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

    describe('compareSale', () => {
      it('Should forward to saleService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(saleService, 'compareSale');
        comp.compareSale(entity, entity2);
        expect(saleService.compareSale).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
