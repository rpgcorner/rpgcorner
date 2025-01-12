import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { IPurchasedStock } from '../purchased-stock.model';

@Component({
  standalone: true,
  selector: 'jhi-purchased-stock-detail',
  templateUrl: './purchased-stock-detail.component.html',
  imports: [SharedModule, RouterModule],
})
export class PurchasedStockDetailComponent {
  purchasedStock = input<IPurchasedStock | null>(null);

  previousState(): void {
    window.history.back();
  }
}
