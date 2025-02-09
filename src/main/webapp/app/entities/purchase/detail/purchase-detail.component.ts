import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { FormatMediumDatePipe } from 'app/shared/date';
import { IPurchase } from '../purchase.model';

@Component({
  standalone: true,
  selector: 'jhi-purchase-detail',
  templateUrl: './purchase-detail.component.html',
  imports: [SharedModule, RouterModule, FormatMediumDatePipe],
})
export class PurchaseDetailComponent {
  purchase = input<IPurchase | null>(null);

  previousState(): void {
    window.history.back();
  }
}
