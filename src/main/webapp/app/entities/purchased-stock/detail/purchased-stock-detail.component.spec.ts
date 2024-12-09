import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { PurchasedStockDetailComponent } from './purchased-stock-detail.component';

describe('PurchasedStock Management Detail Component', () => {
  let comp: PurchasedStockDetailComponent;
  let fixture: ComponentFixture<PurchasedStockDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PurchasedStockDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./purchased-stock-detail.component').then(m => m.PurchasedStockDetailComponent),
              resolve: { purchasedStock: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(PurchasedStockDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PurchasedStockDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load purchasedStock on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', PurchasedStockDetailComponent);

      // THEN
      expect(instance.purchasedStock()).toEqual(expect.objectContaining({ id: 123 }));
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
