import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { DisposedStockDetailComponent } from './disposed-stock-detail.component';

describe('DisposedStock Management Detail Component', () => {
  let comp: DisposedStockDetailComponent;
  let fixture: ComponentFixture<DisposedStockDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DisposedStockDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./disposed-stock-detail.component').then(m => m.DisposedStockDetailComponent),
              resolve: { disposedStock: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(DisposedStockDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DisposedStockDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load disposedStock on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', DisposedStockDetailComponent);

      // THEN
      expect(instance.disposedStock()).toEqual(expect.objectContaining({ id: 123 }));
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
