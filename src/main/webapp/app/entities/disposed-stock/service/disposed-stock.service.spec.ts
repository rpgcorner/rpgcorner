import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { IDisposedStock } from '../disposed-stock.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../disposed-stock.test-samples';

import { DisposedStockService } from './disposed-stock.service';

const requireRestSample: IDisposedStock = {
  ...sampleWithRequiredData,
};

describe('DisposedStock Service', () => {
  let service: DisposedStockService;
  let httpMock: HttpTestingController;
  let expectedResult: IDisposedStock | IDisposedStock[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(DisposedStockService);
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

    it('should create a DisposedStock', () => {
      const disposedStock = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(disposedStock).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a DisposedStock', () => {
      const disposedStock = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(disposedStock).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a DisposedStock', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of DisposedStock', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a DisposedStock', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addDisposedStockToCollectionIfMissing', () => {
      it('should add a DisposedStock to an empty array', () => {
        const disposedStock: IDisposedStock = sampleWithRequiredData;
        expectedResult = service.addDisposedStockToCollectionIfMissing([], disposedStock);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(disposedStock);
      });

      it('should not add a DisposedStock to an array that contains it', () => {
        const disposedStock: IDisposedStock = sampleWithRequiredData;
        const disposedStockCollection: IDisposedStock[] = [
          {
            ...disposedStock,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addDisposedStockToCollectionIfMissing(disposedStockCollection, disposedStock);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a DisposedStock to an array that doesn't contain it", () => {
        const disposedStock: IDisposedStock = sampleWithRequiredData;
        const disposedStockCollection: IDisposedStock[] = [sampleWithPartialData];
        expectedResult = service.addDisposedStockToCollectionIfMissing(disposedStockCollection, disposedStock);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(disposedStock);
      });

      it('should add only unique DisposedStock to an array', () => {
        const disposedStockArray: IDisposedStock[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const disposedStockCollection: IDisposedStock[] = [sampleWithRequiredData];
        expectedResult = service.addDisposedStockToCollectionIfMissing(disposedStockCollection, ...disposedStockArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const disposedStock: IDisposedStock = sampleWithRequiredData;
        const disposedStock2: IDisposedStock = sampleWithPartialData;
        expectedResult = service.addDisposedStockToCollectionIfMissing([], disposedStock, disposedStock2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(disposedStock);
        expect(expectedResult).toContain(disposedStock2);
      });

      it('should accept null and undefined values', () => {
        const disposedStock: IDisposedStock = sampleWithRequiredData;
        expectedResult = service.addDisposedStockToCollectionIfMissing([], null, disposedStock, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(disposedStock);
      });

      it('should return initial array if no DisposedStock is added', () => {
        const disposedStockCollection: IDisposedStock[] = [sampleWithRequiredData];
        expectedResult = service.addDisposedStockToCollectionIfMissing(disposedStockCollection, undefined, null);
        expect(expectedResult).toEqual(disposedStockCollection);
      });
    });

    describe('compareDisposedStock', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareDisposedStock(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareDisposedStock(entity1, entity2);
        const compareResult2 = service.compareDisposedStock(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareDisposedStock(entity1, entity2);
        const compareResult2 = service.compareDisposedStock(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareDisposedStock(entity1, entity2);
        const compareResult2 = service.compareDisposedStock(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
