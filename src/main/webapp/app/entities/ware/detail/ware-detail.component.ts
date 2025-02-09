import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { FormatMediumDatePipe } from 'app/shared/date';
import { IWare } from '../ware.model';

@Component({
  standalone: true,
  selector: 'jhi-ware-detail',
  templateUrl: './ware-detail.component.html',
  imports: [SharedModule, RouterModule, FormatMediumDatePipe],
})
export class WareDetailComponent {
  ware = input<IWare | null>(null);

  previousState(): void {
    window.history.back();
  }
}
