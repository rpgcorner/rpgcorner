import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatePipe, FormatMediumDatetimePipe } from 'app/shared/date';
import { IContact } from '../contact.model';

@Component({
  standalone: true,
  selector: 'jhi-contact-detail',
  templateUrl: './contact-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class ContactDetailComponent {
  contact = input<IContact | null>(null);

  previousState(): void {
    window.history.back();
  }
}
