import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IWare } from '../ware.model';
import { WareService } from '../service/ware.service';

@Component({
  standalone: true,
  templateUrl: './ware-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class WareDeleteDialogComponent {
  ware?: IWare;

  protected wareService = inject(WareService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.wareService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
