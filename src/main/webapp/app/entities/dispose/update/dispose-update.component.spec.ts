import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/service/user.service';
import { DisposeService } from '../service/dispose.service';
import { IDispose } from '../dispose.model';
import { DisposeFormService } from './dispose-form.service';

import { DisposeUpdateComponent } from './dispose-update.component';

describe('Dispose Management Update Component', () => {
  let comp: DisposeUpdateComponent;
  let fixture: ComponentFixture<DisposeUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let disposeFormService: DisposeFormService;
  let disposeService: DisposeService;
  let userService: UserService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [DisposeUpdateComponent],
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
      .overrideTemplate(DisposeUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(DisposeUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    disposeFormService = TestBed.inject(DisposeFormService);
    disposeService = TestBed.inject(DisposeService);
    userService = TestBed.inject(UserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call User query and add missing value', () => {
      const dispose: IDispose = { id: 456 };
      const disposedByUser: IUser = { id: 19444 };
      dispose.disposedByUser = disposedByUser;

      const userCollection: IUser[] = [{ id: 24282 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [disposedByUser];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ dispose });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining),
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const dispose: IDispose = { id: 456 };
      const disposedByUser: IUser = { id: 25861 };
      dispose.disposedByUser = disposedByUser;

      activatedRoute.data = of({ dispose });
      comp.ngOnInit();

      expect(comp.usersSharedCollection).toContain(disposedByUser);
      expect(comp.dispose).toEqual(dispose);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDispose>>();
      const dispose = { id: 123 };
      jest.spyOn(disposeFormService, 'getDispose').mockReturnValue(dispose);
      jest.spyOn(disposeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ dispose });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: dispose }));
      saveSubject.complete();

      // THEN
      expect(disposeFormService.getDispose).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(disposeService.update).toHaveBeenCalledWith(expect.objectContaining(dispose));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDispose>>();
      const dispose = { id: 123 };
      jest.spyOn(disposeFormService, 'getDispose').mockReturnValue({ id: null });
      jest.spyOn(disposeService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ dispose: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: dispose }));
      saveSubject.complete();

      // THEN
      expect(disposeFormService.getDispose).toHaveBeenCalled();
      expect(disposeService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDispose>>();
      const dispose = { id: 123 };
      jest.spyOn(disposeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ dispose });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(disposeService.update).toHaveBeenCalled();
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
  });
});
