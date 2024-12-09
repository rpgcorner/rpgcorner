import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatePipe, FormatMediumDatetimePipe } from 'app/shared/date';
import { IDisposedStock } from '../disposed-stock.model';

@Component({
  standalone: true,
  selector: 'jhi-disposed-stock-detail',
  templateUrl: './disposed-stock-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class DisposedStockDetailComponent {
  disposedStock = input<IDisposedStock | null>(null);

  previousState(): void {
    window.history.back();
  }
}
