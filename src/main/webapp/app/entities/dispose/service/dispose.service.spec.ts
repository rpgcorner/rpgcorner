import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { DATE_FORMAT } from 'app/config/input.constants';
import { IDispose } from '../dispose.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../dispose.test-samples';

import { DisposeService, RestDispose } from './dispose.service';

const requireRestSample: RestDispose = {
  ...sampleWithRequiredData,
  disposeDate: sampleWithRequiredData.disposeDate?.format(DATE_FORMAT),
};

describe('Dispose Service', () => {
  let service: DisposeService;
  let httpMock: HttpTestingController;
  let expectedResult: IDispose | IDispose[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(DisposeService);
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

    it('should create a Dispose', () => {
      const dispose = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(dispose).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Dispose', () => {
      const dispose = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(dispose).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Dispose', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Dispose', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Dispose', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addDisposeToCollectionIfMissing', () => {
      it('should add a Dispose to an empty array', () => {
        const dispose: IDispose = sampleWithRequiredData;
        expectedResult = service.addDisposeToCollectionIfMissing([], dispose);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(dispose);
      });

      it('should not add a Dispose to an array that contains it', () => {
        const dispose: IDispose = sampleWithRequiredData;
        const disposeCollection: IDispose[] = [
          {
            ...dispose,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addDisposeToCollectionIfMissing(disposeCollection, dispose);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Dispose to an array that doesn't contain it", () => {
        const dispose: IDispose = sampleWithRequiredData;
        const disposeCollection: IDispose[] = [sampleWithPartialData];
        expectedResult = service.addDisposeToCollectionIfMissing(disposeCollection, dispose);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(dispose);
      });

      it('should add only unique Dispose to an array', () => {
        const disposeArray: IDispose[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const disposeCollection: IDispose[] = [sampleWithRequiredData];
        expectedResult = service.addDisposeToCollectionIfMissing(disposeCollection, ...disposeArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const dispose: IDispose = sampleWithRequiredData;
        const dispose2: IDispose = sampleWithPartialData;
        expectedResult = service.addDisposeToCollectionIfMissing([], dispose, dispose2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(dispose);
        expect(expectedResult).toContain(dispose2);
      });

      it('should accept null and undefined values', () => {
        const dispose: IDispose = sampleWithRequiredData;
        expectedResult = service.addDisposeToCollectionIfMissing([], null, dispose, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(dispose);
      });

      it('should return initial array if no Dispose is added', () => {
        const disposeCollection: IDispose[] = [sampleWithRequiredData];
        expectedResult = service.addDisposeToCollectionIfMissing(disposeCollection, undefined, null);
        expect(expectedResult).toEqual(disposeCollection);
      });
    });

    describe('compareDispose', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareDispose(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareDispose(entity1, entity2);
        const compareResult2 = service.compareDispose(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareDispose(entity1, entity2);
        const compareResult2 = service.compareDispose(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareDispose(entity1, entity2);
        const compareResult2 = service.compareDispose(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
