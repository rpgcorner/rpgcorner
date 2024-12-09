import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { PurchaseDetailComponent } from './purchase-detail.component';

describe('Purchase Management Detail Component', () => {
  let comp: PurchaseDetailComponent;
  let fixture: ComponentFixture<PurchaseDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PurchaseDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./purchase-detail.component').then(m => m.PurchaseDetailComponent),
              resolve: { purchase: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(PurchaseDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PurchaseDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load purchase on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', PurchaseDetailComponent);

      // THEN
      expect(instance.purchase()).toEqual(expect.objectContaining({ id: 123 }));
    });
  });

  describe('PreviousState', () => {
    it('Should navigate to previous state', () => {
      jest.spyOn(window.history, 'back');
      comp.previousState();
      expect(window.history.back).toHaveBeenCalled();
    });
  });
});
