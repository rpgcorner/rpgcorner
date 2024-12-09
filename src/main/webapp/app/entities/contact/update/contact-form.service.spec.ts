import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../contact.test-samples';

import { ContactFormService } from './contact-form.service';

describe('Contact Form Service', () => {
  let service: ContactFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ContactFormService);
  });

  describe('Service methods', () => {
    describe('createContactFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createContactFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            companyName: expect.any(Object),
            taxNumber: expect.any(Object),
            contactName: expect.any(Object),
            address: expect.any(Object),
            email: expect.any(Object),
            fax: expect.any(Object),
            mobile: expect.any(Object),
            phone: expect.any(Object),
            note: expect.any(Object),
            supplier: expect.any(Object),
          }),
        );
      });

      it('passing IContact should create a new form with FormGroup', () => {
        const formGroup = service.createContactFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            companyName: expect.any(Object),
            taxNumber: expect.any(Object),
            contactName: expect.any(Object),
            address: expect.any(Object),
            email: expect.any(Object),
            fax: expect.any(Object),
            mobile: expect.any(Object),
            phone: expect.any(Object),
            note: expect.any(Object),
            supplier: expect.any(Object),
          }),
        );
      });
    });

    describe('getContact', () => {
      it('should return NewContact for default Contact initial value', () => {
        const formGroup = service.createContactFormGroup(sampleWithNewData);

        const contact = service.getContact(formGroup) as any;

        expect(contact).toMatchObject(sampleWithNewData);
      });

      it('should return NewContact for empty Contact initial value', () => {
        const formGroup = service.createContactFormGroup();

        const contact = service.getContact(formGroup) as any;

        expect(contact).toMatchObject({});
      });

      it('should return IContact', () => {
        const formGroup = service.createContactFormGroup(sampleWithRequiredData);

        const contact = service.getContact(formGroup) as any;

        expect(contact).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IContact should not enable id FormControl', () => {
        const formGroup = service.createContactFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewContact should disable id FormControl', () => {
        const formGroup = service.createContactFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
