import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { FormatMediumDatePipe } from 'app/shared/date';
import { IProductReturn } from '../product-return.model';

@Component({
  standalone: true,
  selector: 'jhi-product-return-detail',
  templateUrl: './product-return-detail.component.html',
  imports: [SharedModule, RouterModule, FormatMediumDatePipe],
})
export class ProductReturnDetailComponent {
  productReturn = input<IProductReturn | null>(null);

  previousState(): void {
    window.history.back();
  }
}
