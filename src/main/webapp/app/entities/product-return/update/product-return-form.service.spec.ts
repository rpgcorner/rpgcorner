import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../product-return.test-samples';

import { ProductReturnFormService } from './product-return-form.service';

describe('ProductReturn Form Service', () => {
  let service: ProductReturnFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ProductReturnFormService);
  });

  describe('Service methods', () => {
    describe('createProductReturnFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createProductReturnFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            returnDate: expect.any(Object),
            note: expect.any(Object),
            sale: expect.any(Object),
            returnedByUser: expect.any(Object),
            returnedByCustomer: expect.any(Object),
          }),
        );
      });

      it('passing IProductReturn should create a new form with FormGroup', () => {
        const formGroup = service.createProductReturnFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            returnDate: expect.any(Object),
            note: expect.any(Object),
            sale: expect.any(Object),
            returnedByUser: expect.any(Object),
            returnedByCustomer: expect.any(Object),
          }),
        );
      });
    });

    describe('getProductReturn', () => {
      it('should return NewProductReturn for default ProductReturn initial value', () => {
        const formGroup = service.createProductReturnFormGroup(sampleWithNewData);

        const productReturn = service.getProductReturn(formGroup) as any;

        expect(productReturn).toMatchObject(sampleWithNewData);
      });

      it('should return NewProductReturn for empty ProductReturn initial value', () => {
        const formGroup = service.createProductReturnFormGroup();

        const productReturn = service.getProductReturn(formGroup) as any;

        expect(productReturn).toMatchObject({});
      });

      it('should return IProductReturn', () => {
        const formGroup = service.createProductReturnFormGroup(sampleWithRequiredData);

        const productReturn = service.getProductReturn(formGroup) as any;

        expect(productReturn).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IProductReturn should not enable id FormControl', () => {
        const formGroup = service.createProductReturnFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewProductReturn should disable id FormControl', () => {
        const formGroup = service.createProductReturnFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
