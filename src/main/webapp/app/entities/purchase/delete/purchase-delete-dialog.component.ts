import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IPurchase } from '../purchase.model';
import { PurchaseService } from '../service/purchase.service';

@Component({
  standalone: true,
  templateUrl: './purchase-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class PurchaseDeleteDialogComponent {
  purchase?: IPurchase;

  protected purchaseService = inject(PurchaseService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.purchaseService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
