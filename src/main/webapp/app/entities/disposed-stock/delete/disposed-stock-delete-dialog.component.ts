import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IDisposedStock } from '../disposed-stock.model';
import { DisposedStockService } from '../service/disposed-stock.service';

@Component({
  standalone: true,
  templateUrl: './disposed-stock-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class DisposedStockDeleteDialogComponent {
  disposedStock?: IDisposedStock;

  protected disposedStockService = inject(DisposedStockService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.disposedStockService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
