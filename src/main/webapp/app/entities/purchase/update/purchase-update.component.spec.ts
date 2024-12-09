import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/service/user.service';
import { ISupplier } from 'app/entities/supplier/supplier.model';
import { SupplierService } from 'app/entities/supplier/service/supplier.service';
import { IPurchase } from '../purchase.model';
import { PurchaseService } from '../service/purchase.service';
import { PurchaseFormService } from './purchase-form.service';

import { PurchaseUpdateComponent } from './purchase-update.component';

describe('Purchase Management Update Component', () => {
  let comp: PurchaseUpdateComponent;
  let fixture: ComponentFixture<PurchaseUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let purchaseFormService: PurchaseFormService;
  let purchaseService: PurchaseService;
  let userService: UserService;
  let supplierService: SupplierService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [PurchaseUpdateComponent],
      providers: [
        provideHttpClient(),
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(PurchaseUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(PurchaseUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    purchaseFormService = TestBed.inject(PurchaseFormService);
    purchaseService = TestBed.inject(PurchaseService);
    userService = TestBed.inject(UserService);
    supplierService = TestBed.inject(SupplierService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call User query and add missing value', () => {
      const purchase: IPurchase = { id: 456 };
      const purchasedByUser: IUser = { id: 17991 };
      purchase.purchasedByUser = purchasedByUser;

      const userCollection: IUser[] = [{ id: 9944 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [purchasedByUser];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ purchase });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining),
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Supplier query and add missing value', () => {
      const purchase: IPurchase = { id: 456 };
      const purchasedFromSupplier: ISupplier = { id: 25963 };
      purchase.purchasedFromSupplier = purchasedFromSupplier;

      const supplierCollection: ISupplier[] = [{ id: 3 }];
      jest.spyOn(supplierService, 'query').mockReturnValue(of(new HttpResponse({ body: supplierCollection })));
      const additionalSuppliers = [purchasedFromSupplier];
      const expectedCollection: ISupplier[] = [...additionalSuppliers, ...supplierCollection];
      jest.spyOn(supplierService, 'addSupplierToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ purchase });
      comp.ngOnInit();

      expect(supplierService.query).toHaveBeenCalled();
      expect(supplierService.addSupplierToCollectionIfMissing).toHaveBeenCalledWith(
        supplierCollection,
        ...additionalSuppliers.map(expect.objectContaining),
      );
      expect(comp.suppliersSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const purchase: IPurchase = { id: 456 };
      const purchasedByUser: IUser = { id: 10561 };
      purchase.purchasedByUser = purchasedByUser;
      const purchasedFromSupplier: ISupplier = { id: 10432 };
      purchase.purchasedFromSupplier = purchasedFromSupplier;

      activatedRoute.data = of({ purchase });
      comp.ngOnInit();

      expect(comp.usersSharedCollection).toContain(purchasedByUser);
      expect(comp.suppliersSharedCollection).toContain(purchasedFromSupplier);
      expect(comp.purchase).toEqual(purchase);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPurchase>>();
      const purchase = { id: 123 };
      jest.spyOn(purchaseFormService, 'getPurchase').mockReturnValue(purchase);
      jest.spyOn(purchaseService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ purchase });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: purchase }));
      saveSubject.complete();

      // THEN
      expect(purchaseFormService.getPurchase).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(purchaseService.update).toHaveBeenCalledWith(expect.objectContaining(purchase));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPurchase>>();
      const purchase = { id: 123 };
      jest.spyOn(purchaseFormService, 'getPurchase').mockReturnValue({ id: null });
      jest.spyOn(purchaseService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ purchase: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: purchase }));
      saveSubject.complete();

      // THEN
      expect(purchaseFormService.getPurchase).toHaveBeenCalled();
      expect(purchaseService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPurchase>>();
      const purchase = { id: 123 };
      jest.spyOn(purchaseService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ purchase });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(purchaseService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareUser', () => {
      it('Should forward to userService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(userService, 'compareUser');
        comp.compareUser(entity, entity2);
        expect(userService.compareUser).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareSupplier', () => {
      it('Should forward to supplierService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(supplierService, 'compareSupplier');
        comp.compareSupplier(entity, entity2);
        expect(supplierService.compareSupplier).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
