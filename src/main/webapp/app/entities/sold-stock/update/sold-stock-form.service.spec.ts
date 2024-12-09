import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../sold-stock.test-samples';

import { SoldStockFormService } from './sold-stock-form.service';

describe('SoldStock Form Service', () => {
  let service: SoldStockFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SoldStockFormService);
  });

  describe('Service methods', () => {
    describe('createSoldStockFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createSoldStockFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            supplie: expect.any(Object),
            unitPrice: expect.any(Object),
            returnedSupplie: expect.any(Object),
            soldWare: expect.any(Object),
            sale: expect.any(Object),
          }),
        );
      });

      it('passing ISoldStock should create a new form with FormGroup', () => {
        const formGroup = service.createSoldStockFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            supplie: expect.any(Object),
            unitPrice: expect.any(Object),
            returnedSupplie: expect.any(Object),
            soldWare: expect.any(Object),
            sale: expect.any(Object),
          }),
        );
      });
    });

    describe('getSoldStock', () => {
      it('should return NewSoldStock for default SoldStock initial value', () => {
        const formGroup = service.createSoldStockFormGroup(sampleWithNewData);

        const soldStock = service.getSoldStock(formGroup) as any;

        expect(soldStock).toMatchObject(sampleWithNewData);
      });

      it('should return NewSoldStock for empty SoldStock initial value', () => {
        const formGroup = service.createSoldStockFormGroup();

        const soldStock = service.getSoldStock(formGroup) as any;

        expect(soldStock).toMatchObject({});
      });

      it('should return ISoldStock', () => {
        const formGroup = service.createSoldStockFormGroup(sampleWithRequiredData);

        const soldStock = service.getSoldStock(formGroup) as any;

        expect(soldStock).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ISoldStock should not enable id FormControl', () => {
        const formGroup = service.createSoldStockFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewSoldStock should disable id FormControl', () => {
        const formGroup = service.createSoldStockFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
