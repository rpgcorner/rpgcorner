import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { IDisposedStock } from '../disposed-stock.model';

@Component({
  standalone: true,
  selector: 'jhi-disposed-stock-detail',
  templateUrl: './disposed-stock-detail.component.html',
  imports: [SharedModule, RouterModule],
})
export class DisposedStockDetailComponent {
  disposedStock = input<IDisposedStock | null>(null);

  previousState(): void {
    window.history.back();
  }
}
