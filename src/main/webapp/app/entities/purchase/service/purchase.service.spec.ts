import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { DATE_FORMAT } from 'app/config/input.constants';
import { IPurchase } from '../purchase.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../purchase.test-samples';

import { PurchaseService, RestPurchase } from './purchase.service';

const requireRestSample: RestPurchase = {
  ...sampleWithRequiredData,
  purchaseDate: sampleWithRequiredData.purchaseDate?.format(DATE_FORMAT),
};

describe('Purchase Service', () => {
  let service: PurchaseService;
  let httpMock: HttpTestingController;
  let expectedResult: IPurchase | IPurchase[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(PurchaseService);
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

    it('should create a Purchase', () => {
      const purchase = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(purchase).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Purchase', () => {
      const purchase = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(purchase).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Purchase', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Purchase', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Purchase', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addPurchaseToCollectionIfMissing', () => {
      it('should add a Purchase to an empty array', () => {
        const purchase: IPurchase = sampleWithRequiredData;
        expectedResult = service.addPurchaseToCollectionIfMissing([], purchase);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(purchase);
      });

      it('should not add a Purchase to an array that contains it', () => {
        const purchase: IPurchase = sampleWithRequiredData;
        const purchaseCollection: IPurchase[] = [
          {
            ...purchase,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addPurchaseToCollectionIfMissing(purchaseCollection, purchase);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Purchase to an array that doesn't contain it", () => {
        const purchase: IPurchase = sampleWithRequiredData;
        const purchaseCollection: IPurchase[] = [sampleWithPartialData];
        expectedResult = service.addPurchaseToCollectionIfMissing(purchaseCollection, purchase);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(purchase);
      });

      it('should add only unique Purchase to an array', () => {
        const purchaseArray: IPurchase[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const purchaseCollection: IPurchase[] = [sampleWithRequiredData];
        expectedResult = service.addPurchaseToCollectionIfMissing(purchaseCollection, ...purchaseArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const purchase: IPurchase = sampleWithRequiredData;
        const purchase2: IPurchase = sampleWithPartialData;
        expectedResult = service.addPurchaseToCollectionIfMissing([], purchase, purchase2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(purchase);
        expect(expectedResult).toContain(purchase2);
      });

      it('should accept null and undefined values', () => {
        const purchase: IPurchase = sampleWithRequiredData;
        expectedResult = service.addPurchaseToCollectionIfMissing([], null, purchase, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(purchase);
      });

      it('should return initial array if no Purchase is added', () => {
        const purchaseCollection: IPurchase[] = [sampleWithRequiredData];
        expectedResult = service.addPurchaseToCollectionIfMissing(purchaseCollection, undefined, null);
        expect(expectedResult).toEqual(purchaseCollection);
      });
    });

    describe('comparePurchase', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.comparePurchase(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.comparePurchase(entity1, entity2);
        const compareResult2 = service.comparePurchase(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.comparePurchase(entity1, entity2);
        const compareResult2 = service.comparePurchase(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.comparePurchase(entity1, entity2);
        const compareResult2 = service.comparePurchase(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
