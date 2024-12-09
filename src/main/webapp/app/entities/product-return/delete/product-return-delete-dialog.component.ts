import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IProductReturn } from '../product-return.model';
import { ProductReturnService } from '../service/product-return.service';

@Component({
  standalone: true,
  templateUrl: './product-return-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class ProductReturnDeleteDialogComponent {
  productReturn?: IProductReturn;

  protected productReturnService = inject(ProductReturnService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.productReturnService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
