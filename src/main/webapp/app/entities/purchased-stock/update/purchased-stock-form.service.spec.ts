import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../purchased-stock.test-samples';

import { PurchasedStockFormService } from './purchased-stock-form.service';

describe('PurchasedStock Form Service', () => {
  let service: PurchasedStockFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PurchasedStockFormService);
  });

  describe('Service methods', () => {
    describe('createPurchasedStockFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createPurchasedStockFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            supplie: expect.any(Object),
            unitPrice: expect.any(Object),
            purchasedWare: expect.any(Object),
            purchase: expect.any(Object),
          }),
        );
      });

      it('passing IPurchasedStock should create a new form with FormGroup', () => {
        const formGroup = service.createPurchasedStockFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            supplie: expect.any(Object),
            unitPrice: expect.any(Object),
            purchasedWare: expect.any(Object),
            purchase: expect.any(Object),
          }),
        );
      });
    });

    describe('getPurchasedStock', () => {
      it('should return NewPurchasedStock for default PurchasedStock initial value', () => {
        const formGroup = service.createPurchasedStockFormGroup(sampleWithNewData);

        const purchasedStock = service.getPurchasedStock(formGroup) as any;

        expect(purchasedStock).toMatchObject(sampleWithNewData);
      });

      it('should return NewPurchasedStock for empty PurchasedStock initial value', () => {
        const formGroup = service.createPurchasedStockFormGroup();

        const purchasedStock = service.getPurchasedStock(formGroup) as any;

        expect(purchasedStock).toMatchObject({});
      });

      it('should return IPurchasedStock', () => {
        const formGroup = service.createPurchasedStockFormGroup(sampleWithRequiredData);

        const purchasedStock = service.getPurchasedStock(formGroup) as any;

        expect(purchasedStock).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IPurchasedStock should not enable id FormControl', () => {
        const formGroup = service.createPurchasedStockFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewPurchasedStock should disable id FormControl', () => {
        const formGroup = service.createPurchasedStockFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
