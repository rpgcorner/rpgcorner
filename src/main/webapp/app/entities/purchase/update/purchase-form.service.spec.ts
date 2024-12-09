import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../purchase.test-samples';

import { PurchaseFormService } from './purchase-form.service';

describe('Purchase Form Service', () => {
  let service: PurchaseFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PurchaseFormService);
  });

  describe('Service methods', () => {
    describe('createPurchaseFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createPurchaseFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            purchaseDate: expect.any(Object),
            purchasedByUser: expect.any(Object),
            purchasedFromSupplier: expect.any(Object),
          }),
        );
      });

      it('passing IPurchase should create a new form with FormGroup', () => {
        const formGroup = service.createPurchaseFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            purchaseDate: expect.any(Object),
            purchasedByUser: expect.any(Object),
            purchasedFromSupplier: expect.any(Object),
          }),
        );
      });
    });

    describe('getPurchase', () => {
      it('should return NewPurchase for default Purchase initial value', () => {
        const formGroup = service.createPurchaseFormGroup(sampleWithNewData);

        const purchase = service.getPurchase(formGroup) as any;

        expect(purchase).toMatchObject(sampleWithNewData);
      });

      it('should return NewPurchase for empty Purchase initial value', () => {
        const formGroup = service.createPurchaseFormGroup();

        const purchase = service.getPurchase(formGroup) as any;

        expect(purchase).toMatchObject({});
      });

      it('should return IPurchase', () => {
        const formGroup = service.createPurchaseFormGroup(sampleWithRequiredData);

        const purchase = service.getPurchase(formGroup) as any;

        expect(purchase).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IPurchase should not enable id FormControl', () => {
        const formGroup = service.createPurchaseFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewPurchase should disable id FormControl', () => {
        const formGroup = service.createPurchaseFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
