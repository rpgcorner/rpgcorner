import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { ISale } from 'app/entities/sale/sale.model';
import { SaleService } from 'app/entities/sale/service/sale.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/service/user.service';
import { ICustomer } from 'app/entities/customer/customer.model';
import { CustomerService } from 'app/entities/customer/service/customer.service';
import { IProductReturn } from '../product-return.model';
import { ProductReturnService } from '../service/product-return.service';
import { ProductReturnFormService } from './product-return-form.service';

import { ProductReturnUpdateComponent } from './product-return-update.component';

describe('ProductReturn Management Update Component', () => {
  let comp: ProductReturnUpdateComponent;
  let fixture: ComponentFixture<ProductReturnUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let productReturnFormService: ProductReturnFormService;
  let productReturnService: ProductReturnService;
  let saleService: SaleService;
  let userService: UserService;
  let customerService: CustomerService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ProductReturnUpdateComponent],
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
      .overrideTemplate(ProductReturnUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ProductReturnUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    productReturnFormService = TestBed.inject(ProductReturnFormService);
    productReturnService = TestBed.inject(ProductReturnService);
    saleService = TestBed.inject(SaleService);
    userService = TestBed.inject(UserService);
    customerService = TestBed.inject(CustomerService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Sale query and add missing value', () => {
      const productReturn: IProductReturn = { id: 456 };
      const sale: ISale = { id: 28445 };
      productReturn.sale = sale;

      const saleCollection: ISale[] = [{ id: 7965 }];
      jest.spyOn(saleService, 'query').mockReturnValue(of(new HttpResponse({ body: saleCollection })));
      const additionalSales = [sale];
      const expectedCollection: ISale[] = [...additionalSales, ...saleCollection];
      jest.spyOn(saleService, 'addSaleToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ productReturn });
      comp.ngOnInit();

      expect(saleService.query).toHaveBeenCalled();
      expect(saleService.addSaleToCollectionIfMissing).toHaveBeenCalledWith(
        saleCollection,
        ...additionalSales.map(expect.objectContaining),
      );
      expect(comp.salesSharedCollection).toEqual(expectedCollection);
    });

    it('Should call User query and add missing value', () => {
      const productReturn: IProductReturn = { id: 456 };
      const returnedByUser: IUser = { id: 10893 };
      productReturn.returnedByUser = returnedByUser;

      const userCollection: IUser[] = [{ id: 26017 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [returnedByUser];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ productReturn });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining),
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Customer query and add missing value', () => {
      const productReturn: IProductReturn = { id: 456 };
      const returnedByCustomer: ICustomer = { id: 16488 };
      productReturn.returnedByCustomer = returnedByCustomer;

      const customerCollection: ICustomer[] = [{ id: 26433 }];
      jest.spyOn(customerService, 'query').mockReturnValue(of(new HttpResponse({ body: customerCollection })));
      const additionalCustomers = [returnedByCustomer];
      const expectedCollection: ICustomer[] = [...additionalCustomers, ...customerCollection];
      jest.spyOn(customerService, 'addCustomerToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ productReturn });
      comp.ngOnInit();

      expect(customerService.query).toHaveBeenCalled();
      expect(customerService.addCustomerToCollectionIfMissing).toHaveBeenCalledWith(
        customerCollection,
        ...additionalCustomers.map(expect.objectContaining),
      );
      expect(comp.customersSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const productReturn: IProductReturn = { id: 456 };
      const sale: ISale = { id: 15340 };
      productReturn.sale = sale;
      const returnedByUser: IUser = { id: 16568 };
      productReturn.returnedByUser = returnedByUser;
      const returnedByCustomer: ICustomer = { id: 7795 };
      productReturn.returnedByCustomer = returnedByCustomer;

      activatedRoute.data = of({ productReturn });
      comp.ngOnInit();

      expect(comp.salesSharedCollection).toContain(sale);
      expect(comp.usersSharedCollection).toContain(returnedByUser);
      expect(comp.customersSharedCollection).toContain(returnedByCustomer);
      expect(comp.productReturn).toEqual(productReturn);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IProductReturn>>();
      const productReturn = { id: 123 };
      jest.spyOn(productReturnFormService, 'getProductReturn').mockReturnValue(productReturn);
      jest.spyOn(productReturnService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ productReturn });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: productReturn }));
      saveSubject.complete();

      // THEN
      expect(productReturnFormService.getProductReturn).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(productReturnService.update).toHaveBeenCalledWith(expect.objectContaining(productReturn));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IProductReturn>>();
      const productReturn = { id: 123 };
      jest.spyOn(productReturnFormService, 'getProductReturn').mockReturnValue({ id: null });
      jest.spyOn(productReturnService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ productReturn: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: productReturn }));
      saveSubject.complete();

      // THEN
      expect(productReturnFormService.getProductReturn).toHaveBeenCalled();
      expect(productReturnService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IProductReturn>>();
      const productReturn = { id: 123 };
      jest.spyOn(productReturnService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ productReturn });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(productReturnService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareSale', () => {
      it('Should forward to saleService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(saleService, 'compareSale');
        comp.compareSale(entity, entity2);
        expect(saleService.compareSale).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareUser', () => {
      it('Should forward to userService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(userService, 'compareUser');
        comp.compareUser(entity, entity2);
        expect(userService.compareUser).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareCustomer', () => {
      it('Should forward to customerService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(customerService, 'compareCustomer');
        comp.compareCustomer(entity, entity2);
        expect(customerService.compareCustomer).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
