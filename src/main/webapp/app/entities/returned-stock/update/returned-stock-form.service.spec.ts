import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../returned-stock.test-samples';

import { ReturnedStockFormService } from './returned-stock-form.service';

describe('ReturnedStock Form Service', () => {
  let service: ReturnedStockFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ReturnedStockFormService);
  });

  describe('Service methods', () => {
    describe('createReturnedStockFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createReturnedStockFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            supplie: expect.any(Object),
            unitPrice: expect.any(Object),
            returnedWare: expect.any(Object),
            productReturn: expect.any(Object),
          }),
        );
      });

      it('passing IReturnedStock should create a new form with FormGroup', () => {
        const formGroup = service.createReturnedStockFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            supplie: expect.any(Object),
            unitPrice: expect.any(Object),
            returnedWare: expect.any(Object),
            productReturn: expect.any(Object),
          }),
        );
      });
    });

    describe('getReturnedStock', () => {
      it('should return NewReturnedStock for default ReturnedStock initial value', () => {
        const formGroup = service.createReturnedStockFormGroup(sampleWithNewData);

        const returnedStock = service.getReturnedStock(formGroup) as any;

        expect(returnedStock).toMatchObject(sampleWithNewData);
      });

      it('should return NewReturnedStock for empty ReturnedStock initial value', () => {
        const formGroup = service.createReturnedStockFormGroup();

        const returnedStock = service.getReturnedStock(formGroup) as any;

        expect(returnedStock).toMatchObject({});
      });

      it('should return IReturnedStock', () => {
        const formGroup = service.createReturnedStockFormGroup(sampleWithRequiredData);

        const returnedStock = service.getReturnedStock(formGroup) as any;

        expect(returnedStock).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IReturnedStock should not enable id FormControl', () => {
        const formGroup = service.createReturnedStockFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewReturnedStock should disable id FormControl', () => {
        const formGroup = service.createReturnedStockFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
