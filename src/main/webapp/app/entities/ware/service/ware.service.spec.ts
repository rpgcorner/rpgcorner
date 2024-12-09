import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { DATE_FORMAT } from 'app/config/input.constants';
import { IWare } from '../ware.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../ware.test-samples';

import { RestWare, WareService } from './ware.service';

const requireRestSample: RestWare = {
  ...sampleWithRequiredData,
  created: sampleWithRequiredData.created?.format(DATE_FORMAT),
};

describe('Ware Service', () => {
  let service: WareService;
  let httpMock: HttpTestingController;
  let expectedResult: IWare | IWare[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(WareService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a Ware', () => {
      const ware = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(ware).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Ware', () => {
      const ware = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(ware).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Ware', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Ware', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Ware', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addWareToCollectionIfMissing', () => {
      it('should add a Ware to an empty array', () => {
        const ware: IWare = sampleWithRequiredData;
        expectedResult = service.addWareToCollectionIfMissing([], ware);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(ware);
      });

      it('should not add a Ware to an array that contains it', () => {
        const ware: IWare = sampleWithRequiredData;
        const wareCollection: IWare[] = [
          {
            ...ware,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addWareToCollectionIfMissing(wareCollection, ware);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Ware to an array that doesn't contain it", () => {
        const ware: IWare = sampleWithRequiredData;
        const wareCollection: IWare[] = [sampleWithPartialData];
        expectedResult = service.addWareToCollectionIfMissing(wareCollection, ware);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(ware);
      });

      it('should add only unique Ware to an array', () => {
        const wareArray: IWare[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const wareCollection: IWare[] = [sampleWithRequiredData];
        expectedResult = service.addWareToCollectionIfMissing(wareCollection, ...wareArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const ware: IWare = sampleWithRequiredData;
        const ware2: IWare = sampleWithPartialData;
        expectedResult = service.addWareToCollectionIfMissing([], ware, ware2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(ware);
        expect(expectedResult).toContain(ware2);
      });

      it('should accept null and undefined values', () => {
        const ware: IWare = sampleWithRequiredData;
        expectedResult = service.addWareToCollectionIfMissing([], null, ware, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(ware);
      });

      it('should return initial array if no Ware is added', () => {
        const wareCollection: IWare[] = [sampleWithRequiredData];
        expectedResult = service.addWareToCollectionIfMissing(wareCollection, undefined, null);
        expect(expectedResult).toEqual(wareCollection);
      });
    });

    describe('compareWare', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareWare(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareWare(entity1, entity2);
        const compareResult2 = service.compareWare(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareWare(entity1, entity2);
        const compareResult2 = service.compareWare(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareWare(entity1, entity2);
        const compareResult2 = service.compareWare(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
