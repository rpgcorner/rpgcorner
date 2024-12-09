import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IWare } from 'app/entities/ware/ware.model';
import { WareService } from 'app/entities/ware/service/ware.service';
import { IPurchase } from 'app/entities/purchase/purchase.model';
import { PurchaseService } from 'app/entities/purchase/service/purchase.service';
import { IPurchasedStock } from '../purchased-stock.model';
import { PurchasedStockService } from '../service/purchased-stock.service';
import { PurchasedStockFormService } from './purchased-stock-form.service';

import { PurchasedStockUpdateComponent } from './purchased-stock-update.component';

describe('PurchasedStock Management Update Component', () => {
  let comp: PurchasedStockUpdateComponent;
  let fixture: ComponentFixture<PurchasedStockUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let purchasedStockFormService: PurchasedStockFormService;
  let purchasedStockService: PurchasedStockService;
  let wareService: WareService;
  let purchaseService: PurchaseService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [PurchasedStockUpdateComponent],
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
      .overrideTemplate(PurchasedStockUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(PurchasedStockUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    purchasedStockFormService = TestBed.inject(PurchasedStockFormService);
    purchasedStockService = TestBed.inject(PurchasedStockService);
    wareService = TestBed.inject(WareService);
    purchaseService = TestBed.inject(PurchaseService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call purchasedWare query and add missing value', () => {
      const purchasedStock: IPurchasedStock = { id: 456 };
      const purchasedWare: IWare = { id: 26078 };
      purchasedStock.purchasedWare = purchasedWare;

      const purchasedWareCollection: IWare[] = [{ id: 19853 }];
      jest.spyOn(wareService, 'query').mockReturnValue(of(new HttpResponse({ body: purchasedWareCollection })));
      const expectedCollection: IWare[] = [purchasedWare, ...purchasedWareCollection];
      jest.spyOn(wareService, 'addWareToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ purchasedStock });
      comp.ngOnInit();

      expect(wareService.query).toHaveBeenCalled();
      expect(wareService.addWareToCollectionIfMissing).toHaveBeenCalledWith(purchasedWareCollection, purchasedWare);
      expect(comp.purchasedWaresCollection).toEqual(expectedCollection);
    });

    it('Should call Purchase query and add missing value', () => {
      const purchasedStock: IPurchasedStock = { id: 456 };
      const purchase: IPurchase = { id: 14002 };
      purchasedStock.purchase = purchase;

      const purchaseCollection: IPurchase[] = [{ id: 25161 }];
      jest.spyOn(purchaseService, 'query').mockReturnValue(of(new HttpResponse({ body: purchaseCollection })));
      const additionalPurchases = [purchase];
      const expectedCollection: IPurchase[] = [...additionalPurchases, ...purchaseCollection];
      jest.spyOn(purchaseService, 'addPurchaseToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ purchasedStock });
      comp.ngOnInit();

      expect(purchaseService.query).toHaveBeenCalled();
      expect(purchaseService.addPurchaseToCollectionIfMissing).toHaveBeenCalledWith(
        purchaseCollection,
        ...additionalPurchases.map(expect.objectContaining),
      );
      expect(comp.purchasesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const purchasedStock: IPurchasedStock = { id: 456 };
      const purchasedWare: IWare = { id: 6956 };
      purchasedStock.purchasedWare = purchasedWare;
      const purchase: IPurchase = { id: 2100 };
      purchasedStock.purchase = purchase;

      activatedRoute.data = of({ purchasedStock });
      comp.ngOnInit();

      expect(comp.purchasedWaresCollection).toContain(purchasedWare);
      expect(comp.purchasesSharedCollection).toContain(purchase);
      expect(comp.purchasedStock).toEqual(purchasedStock);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPurchasedStock>>();
      const purchasedStock = { id: 123 };
      jest.spyOn(purchasedStockFormService, 'getPurchasedStock').mockReturnValue(purchasedStock);
      jest.spyOn(purchasedStockService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ purchasedStock });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: purchasedStock }));
      saveSubject.complete();

      // THEN
      expect(purchasedStockFormService.getPurchasedStock).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(purchasedStockService.update).toHaveBeenCalledWith(expect.objectContaining(purchasedStock));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPurchasedStock>>();
      const purchasedStock = { id: 123 };
      jest.spyOn(purchasedStockFormService, 'getPurchasedStock').mockReturnValue({ id: null });
      jest.spyOn(purchasedStockService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ purchasedStock: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: purchasedStock }));
      saveSubject.complete();

      // THEN
      expect(purchasedStockFormService.getPurchasedStock).toHaveBeenCalled();
      expect(purchasedStockService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPurchasedStock>>();
      const purchasedStock = { id: 123 };
      jest.spyOn(purchasedStockService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ purchasedStock });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(purchasedStockService.update).toHaveBeenCalled();
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

    describe('comparePurchase', () => {
      it('Should forward to purchaseService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(purchaseService, 'comparePurchase');
        comp.comparePurchase(entity, entity2);
        expect(purchaseService.comparePurchase).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
