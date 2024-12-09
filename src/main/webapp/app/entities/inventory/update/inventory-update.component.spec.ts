import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IWare } from 'app/entities/ware/ware.model';
import { WareService } from 'app/entities/ware/service/ware.service';
import { InventoryService } from '../service/inventory.service';
import { IInventory } from '../inventory.model';
import { InventoryFormService } from './inventory-form.service';

import { InventoryUpdateComponent } from './inventory-update.component';

describe('Inventory Management Update Component', () => {
  let comp: InventoryUpdateComponent;
  let fixture: ComponentFixture<InventoryUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let inventoryFormService: InventoryFormService;
  let inventoryService: InventoryService;
  let wareService: WareService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [InventoryUpdateComponent],
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
      .overrideTemplate(InventoryUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(InventoryUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    inventoryFormService = TestBed.inject(InventoryFormService);
    inventoryService = TestBed.inject(InventoryService);
    wareService = TestBed.inject(WareService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call ware query and add missing value', () => {
      const inventory: IInventory = { id: 456 };
      const ware: IWare = { id: 27982 };
      inventory.ware = ware;

      const wareCollection: IWare[] = [{ id: 11537 }];
      jest.spyOn(wareService, 'query').mockReturnValue(of(new HttpResponse({ body: wareCollection })));
      const expectedCollection: IWare[] = [ware, ...wareCollection];
      jest.spyOn(wareService, 'addWareToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ inventory });
      comp.ngOnInit();

      expect(wareService.query).toHaveBeenCalled();
      expect(wareService.addWareToCollectionIfMissing).toHaveBeenCalledWith(wareCollection, ware);
      expect(comp.waresCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const inventory: IInventory = { id: 456 };
      const ware: IWare = { id: 17045 };
      inventory.ware = ware;

      activatedRoute.data = of({ inventory });
      comp.ngOnInit();

      expect(comp.waresCollection).toContain(ware);
      expect(comp.inventory).toEqual(inventory);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IInventory>>();
      const inventory = { id: 123 };
      jest.spyOn(inventoryFormService, 'getInventory').mockReturnValue(inventory);
      jest.spyOn(inventoryService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ inventory });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: inventory }));
      saveSubject.complete();

      // THEN
      expect(inventoryFormService.getInventory).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(inventoryService.update).toHaveBeenCalledWith(expect.objectContaining(inventory));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IInventory>>();
      const inventory = { id: 123 };
      jest.spyOn(inventoryFormService, 'getInventory').mockReturnValue({ id: null });
      jest.spyOn(inventoryService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ inventory: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: inventory }));
      saveSubject.complete();

      // THEN
      expect(inventoryFormService.getInventory).toHaveBeenCalled();
      expect(inventoryService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IInventory>>();
      const inventory = { id: 123 };
      jest.spyOn(inventoryService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ inventory });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(inventoryService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareWare', () => {
      it('Should forward to wareService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(wareService, 'compareWare');
        comp.compareWare(entity, entity2);
        expect(wareService.compareWare).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
