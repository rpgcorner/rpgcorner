import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { SoldStockDetailComponent } from './sold-stock-detail.component';

describe('SoldStock Management Detail Component', () => {
  let comp: SoldStockDetailComponent;
  let fixture: ComponentFixture<SoldStockDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SoldStockDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./sold-stock-detail.component').then(m => m.SoldStockDetailComponent),
              resolve: { soldStock: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(SoldStockDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SoldStockDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load soldStock on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', SoldStockDetailComponent);

      // THEN
      expect(instance.soldStock()).toEqual(expect.objectContaining({ id: 123 }));
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
