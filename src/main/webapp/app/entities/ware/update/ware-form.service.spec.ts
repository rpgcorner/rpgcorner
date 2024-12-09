import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../ware.test-samples';

import { WareFormService } from './ware-form.service';

describe('Ware Form Service', () => {
  let service: WareFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(WareFormService);
  });

  describe('Service methods', () => {
    describe('createWareFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createWareFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            active: expect.any(Object),
            created: expect.any(Object),
            name: expect.any(Object),
            description: expect.any(Object),
            productCode: expect.any(Object),
            mainCategory: expect.any(Object),
            subCategory: expect.any(Object),
          }),
        );
      });

      it('passing IWare should create a new form with FormGroup', () => {
        const formGroup = service.createWareFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            active: expect.any(Object),
            created: expect.any(Object),
            name: expect.any(Object),
            description: expect.any(Object),
            productCode: expect.any(Object),
            mainCategory: expect.any(Object),
            subCategory: expect.any(Object),
          }),
        );
      });
    });

    describe('getWare', () => {
      it('should return NewWare for default Ware initial value', () => {
        const formGroup = service.createWareFormGroup(sampleWithNewData);

        const ware = service.getWare(formGroup) as any;

        expect(ware).toMatchObject(sampleWithNewData);
      });

      it('should return NewWare for empty Ware initial value', () => {
        const formGroup = service.createWareFormGroup();

        const ware = service.getWare(formGroup) as any;

        expect(ware).toMatchObject({});
      });

      it('should return IWare', () => {
        const formGroup = service.createWareFormGroup(sampleWithRequiredData);

        const ware = service.getWare(formGroup) as any;

        expect(ware).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IWare should not enable id FormControl', () => {
        const formGroup = service.createWareFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewWare should disable id FormControl', () => {
        const formGroup = service.createWareFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
