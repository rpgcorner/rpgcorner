import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IPurchasedStock } from '../purchased-stock.model';
import { PurchasedStockService } from '../service/purchased-stock.service';

@Component({
  standalone: true,
  templateUrl: './purchased-stock-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class PurchasedStockDeleteDialogComponent {
  purchasedStock?: IPurchasedStock;

  protected purchasedStockService = inject(PurchasedStockService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.purchasedStockService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
