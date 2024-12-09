import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../disposed-stock.test-samples';

import { DisposedStockFormService } from './disposed-stock-form.service';

describe('DisposedStock Form Service', () => {
  let service: DisposedStockFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(DisposedStockFormService);
  });

  describe('Service methods', () => {
    describe('createDisposedStockFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createDisposedStockFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            supplie: expect.any(Object),
            unitPrice: expect.any(Object),
            disposedWare: expect.any(Object),
            dispose: expect.any(Object),
          }),
        );
      });

      it('passing IDisposedStock should create a new form with FormGroup', () => {
        const formGroup = service.createDisposedStockFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            supplie: expect.any(Object),
            unitPrice: expect.any(Object),
            disposedWare: expect.any(Object),
            dispose: expect.any(Object),
          }),
        );
      });
    });

    describe('getDisposedStock', () => {
      it('should return NewDisposedStock for default DisposedStock initial value', () => {
        const formGroup = service.createDisposedStockFormGroup(sampleWithNewData);

        const disposedStock = service.getDisposedStock(formGroup) as any;

        expect(disposedStock).toMatchObject(sampleWithNewData);
      });

      it('should return NewDisposedStock for empty DisposedStock initial value', () => {
        const formGroup = service.createDisposedStockFormGroup();

        const disposedStock = service.getDisposedStock(formGroup) as any;

        expect(disposedStock).toMatchObject({});
      });

      it('should return IDisposedStock', () => {
        const formGroup = service.createDisposedStockFormGroup(sampleWithRequiredData);

        const disposedStock = service.getDisposedStock(formGroup) as any;

        expect(disposedStock).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IDisposedStock should not enable id FormControl', () => {
        const formGroup = service.createDisposedStockFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewDisposedStock should disable id FormControl', () => {
        const formGroup = service.createDisposedStockFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
