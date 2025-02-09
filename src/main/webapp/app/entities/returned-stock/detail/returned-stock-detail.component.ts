import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { IReturnedStock } from '../returned-stock.model';

@Component({
  standalone: true,
  selector: 'jhi-returned-stock-detail',
  templateUrl: './returned-stock-detail.component.html',
  imports: [SharedModule, RouterModule],
})
export class ReturnedStockDetailComponent {
  returnedStock = input<IReturnedStock | null>(null);

  previousState(): void {
    window.history.back();
  }
}
