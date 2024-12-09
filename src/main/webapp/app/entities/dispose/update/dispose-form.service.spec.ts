import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../dispose.test-samples';

import { DisposeFormService } from './dispose-form.service';

describe('Dispose Form Service', () => {
  let service: DisposeFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(DisposeFormService);
  });

  describe('Service methods', () => {
    describe('createDisposeFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createDisposeFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            disposeDate: expect.any(Object),
            note: expect.any(Object),
            disposedByUser: expect.any(Object),
          }),
        );
      });

      it('passing IDispose should create a new form with FormGroup', () => {
        const formGroup = service.createDisposeFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            disposeDate: expect.any(Object),
            note: expect.any(Object),
            disposedByUser: expect.any(Object),
          }),
        );
      });
    });

    describe('getDispose', () => {
      it('should return NewDispose for default Dispose initial value', () => {
        const formGroup = service.createDisposeFormGroup(sampleWithNewData);

        const dispose = service.getDispose(formGroup) as any;

        expect(dispose).toMatchObject(sampleWithNewData);
      });

      it('should return NewDispose for empty Dispose initial value', () => {
        const formGroup = service.createDisposeFormGroup();

        const dispose = service.getDispose(formGroup) as any;

        expect(dispose).toMatchObject({});
      });

      it('should return IDispose', () => {
        const formGroup = service.createDisposeFormGroup(sampleWithRequiredData);

        const dispose = service.getDispose(formGroup) as any;

        expect(dispose).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IDispose should not enable id FormControl', () => {
        const formGroup = service.createDisposeFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewDispose should disable id FormControl', () => {
        const formGroup = service.createDisposeFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
