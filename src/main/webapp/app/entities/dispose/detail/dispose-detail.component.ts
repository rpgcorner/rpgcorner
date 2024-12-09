import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatePipe, FormatMediumDatetimePipe } from 'app/shared/date';
import { IDispose } from '../dispose.model';

@Component({
  standalone: true,
  selector: 'jhi-dispose-detail',
  templateUrl: './dispose-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class DisposeDetailComponent {
  dispose = input<IDispose | null>(null);

  previousState(): void {
    window.history.back();
  }
}
