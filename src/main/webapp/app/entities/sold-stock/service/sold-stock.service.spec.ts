import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { ISoldStock } from '../sold-stock.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../sold-stock.test-samples';

import { SoldStockService } from './sold-stock.service';

const requireRestSample: ISoldStock = {
  ...sampleWithRequiredData,
};

describe('SoldStock Service', () => {
  let service: SoldStockService;
  let httpMock: HttpTestingController;
  let expectedResult: ISoldStock | ISoldStock[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(SoldStockService);
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

    it('should create a SoldStock', () => {
      const soldStock = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(soldStock).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a SoldStock', () => {
      const soldStock = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(soldStock).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a SoldStock', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of SoldStock', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a SoldStock', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addSoldStockToCollectionIfMissing', () => {
      it('should add a SoldStock to an empty array', () => {
        const soldStock: ISoldStock = sampleWithRequiredData;
        expectedResult = service.addSoldStockToCollectionIfMissing([], soldStock);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(soldStock);
      });

      it('should not add a SoldStock to an array that contains it', () => {
        const soldStock: ISoldStock = sampleWithRequiredData;
        const soldStockCollection: ISoldStock[] = [
          {
            ...soldStock,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addSoldStockToCollectionIfMissing(soldStockCollection, soldStock);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a SoldStock to an array that doesn't contain it", () => {
        const soldStock: ISoldStock = sampleWithRequiredData;
        const soldStockCollection: ISoldStock[] = [sampleWithPartialData];
        expectedResult = service.addSoldStockToCollectionIfMissing(soldStockCollection, soldStock);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(soldStock);
      });

      it('should add only unique SoldStock to an array', () => {
        const soldStockArray: ISoldStock[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const soldStockCollection: ISoldStock[] = [sampleWithRequiredData];
        expectedResult = service.addSoldStockToCollectionIfMissing(soldStockCollection, ...soldStockArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const soldStock: ISoldStock = sampleWithRequiredData;
        const soldStock2: ISoldStock = sampleWithPartialData;
        expectedResult = service.addSoldStockToCollectionIfMissing([], soldStock, soldStock2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(soldStock);
        expect(expectedResult).toContain(soldStock2);
      });

      it('should accept null and undefined values', () => {
        const soldStock: ISoldStock = sampleWithRequiredData;
        expectedResult = service.addSoldStockToCollectionIfMissing([], null, soldStock, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(soldStock);
      });

      it('should return initial array if no SoldStock is added', () => {
        const soldStockCollection: ISoldStock[] = [sampleWithRequiredData];
        expectedResult = service.addSoldStockToCollectionIfMissing(soldStockCollection, undefined, null);
        expect(expectedResult).toEqual(soldStockCollection);
      });
    });

    describe('compareSoldStock', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareSoldStock(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareSoldStock(entity1, entity2);
        const compareResult2 = service.compareSoldStock(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareSoldStock(entity1, entity2);
        const compareResult2 = service.compareSoldStock(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareSoldStock(entity1, entity2);
        const compareResult2 = service.compareSoldStock(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
