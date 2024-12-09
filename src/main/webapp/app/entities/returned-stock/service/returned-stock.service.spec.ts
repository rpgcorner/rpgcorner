import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { IReturnedStock } from '../returned-stock.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../returned-stock.test-samples';

import { ReturnedStockService } from './returned-stock.service';

const requireRestSample: IReturnedStock = {
  ...sampleWithRequiredData,
};

describe('ReturnedStock Service', () => {
  let service: ReturnedStockService;
  let httpMock: HttpTestingController;
  let expectedResult: IReturnedStock | IReturnedStock[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(ReturnedStockService);
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

    it('should create a ReturnedStock', () => {
      const returnedStock = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(returnedStock).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a ReturnedStock', () => {
      const returnedStock = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(returnedStock).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a ReturnedStock', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of ReturnedStock', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a ReturnedStock', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addReturnedStockToCollectionIfMissing', () => {
      it('should add a ReturnedStock to an empty array', () => {
        const returnedStock: IReturnedStock = sampleWithRequiredData;
        expectedResult = service.addReturnedStockToCollectionIfMissing([], returnedStock);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(returnedStock);
      });

      it('should not add a ReturnedStock to an array that contains it', () => {
        const returnedStock: IReturnedStock = sampleWithRequiredData;
        const returnedStockCollection: IReturnedStock[] = [
          {
            ...returnedStock,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addReturnedStockToCollectionIfMissing(returnedStockCollection, returnedStock);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a ReturnedStock to an array that doesn't contain it", () => {
        const returnedStock: IReturnedStock = sampleWithRequiredData;
        const returnedStockCollection: IReturnedStock[] = [sampleWithPartialData];
        expectedResult = service.addReturnedStockToCollectionIfMissing(returnedStockCollection, returnedStock);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(returnedStock);
      });

      it('should add only unique ReturnedStock to an array', () => {
        const returnedStockArray: IReturnedStock[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const returnedStockCollection: IReturnedStock[] = [sampleWithRequiredData];
        expectedResult = service.addReturnedStockToCollectionIfMissing(returnedStockCollection, ...returnedStockArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const returnedStock: IReturnedStock = sampleWithRequiredData;
        const returnedStock2: IReturnedStock = sampleWithPartialData;
        expectedResult = service.addReturnedStockToCollectionIfMissing([], returnedStock, returnedStock2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(returnedStock);
        expect(expectedResult).toContain(returnedStock2);
      });

      it('should accept null and undefined values', () => {
        const returnedStock: IReturnedStock = sampleWithRequiredData;
        expectedResult = service.addReturnedStockToCollectionIfMissing([], null, returnedStock, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(returnedStock);
      });

      it('should return initial array if no ReturnedStock is added', () => {
        const returnedStockCollection: IReturnedStock[] = [sampleWithRequiredData];
        expectedResult = service.addReturnedStockToCollectionIfMissing(returnedStockCollection, undefined, null);
        expect(expectedResult).toEqual(returnedStockCollection);
      });
    });

    describe('compareReturnedStock', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareReturnedStock(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareReturnedStock(entity1, entity2);
        const compareResult2 = service.compareReturnedStock(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareReturnedStock(entity1, entity2);
        const compareResult2 = service.compareReturnedStock(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareReturnedStock(entity1, entity2);
        const compareResult2 = service.compareReturnedStock(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
