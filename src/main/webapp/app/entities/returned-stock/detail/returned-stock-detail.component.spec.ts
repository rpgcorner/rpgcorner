import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { ReturnedStockDetailComponent } from './returned-stock-detail.component';

describe('ReturnedStock Management Detail Component', () => {
  let comp: ReturnedStockDetailComponent;
  let fixture: ComponentFixture<ReturnedStockDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReturnedStockDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./returned-stock-detail.component').then(m => m.ReturnedStockDetailComponent),
              resolve: { returnedStock: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(ReturnedStockDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ReturnedStockDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load returnedStock on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', ReturnedStockDetailComponent);

      // THEN
      expect(instance.returnedStock()).toEqual(expect.objectContaining({ id: 123 }));
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
