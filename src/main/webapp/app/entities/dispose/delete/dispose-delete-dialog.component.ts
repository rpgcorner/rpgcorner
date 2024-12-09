import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IDispose } from '../dispose.model';
import { DisposeService } from '../service/dispose.service';

@Component({
  standalone: true,
  templateUrl: './dispose-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class DisposeDeleteDialogComponent {
  dispose?: IDispose;

  protected disposeService = inject(DisposeService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.disposeService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
