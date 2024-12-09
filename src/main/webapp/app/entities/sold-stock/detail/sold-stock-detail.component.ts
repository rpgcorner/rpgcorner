import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatePipe, FormatMediumDatetimePipe } from 'app/shared/date';
import { ISoldStock } from '../sold-stock.model';

@Component({
  standalone: true,
  selector: 'jhi-sold-stock-detail',
  templateUrl: './sold-stock-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class SoldStockDetailComponent {
  soldStock = input<ISoldStock | null>(null);

  previousState(): void {
    window.history.back();
  }
}
