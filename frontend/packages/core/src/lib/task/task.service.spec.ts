import {
  HttpClientTestingModule,
  HttpTestingController
} from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { CURO_BASE_PATH } from '../curo-base-path';
import { TaskService } from './task.service';

describe('TaskService', () => {
  let service: TaskService;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        {
          provide: CURO_BASE_PATH,
          useValue: '/curo-api'
        }
      ]
    });
    service = TestBed.inject(TaskService);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTestingController.verify();
  });

  describe('getTask', () => {
    it('should get the task for a specific id', () => {
      const id = '1234';

      service.getTask(id).subscribe((data) => expect(data).toEqual({}));

      const req = httpTestingController.expectOne(`/curo-api/tasks/${id}`);
      expect(req.request.method).toEqual('GET');

      req.flush({});
    });

    it('should get the task for a specific id with variables filter', () => {
      const id = '1234';

      service
        .getTask(id, { variables: ['varA', 'varB'] })
        .subscribe((data) => expect(data).toEqual({}));

      const req = httpTestingController.expectOne(
        `/curo-api/tasks/${id}?variables=varA&variables=varB`
      );
      expect(req.request.method).toEqual('GET');

      req.flush({});
    });

    it('should get the task for a specific id with task attributes filter', () => {
      const id = '1234';

      service
        .getTask(id, { attributes: ['attrA', 'attrB'] })
        .subscribe((data) => expect(data).toEqual({}));

      const req = httpTestingController.expectOne(
        `/curo-api/tasks/${id}?attributes=attrA&attributes=attrB`
      );
      expect(req.request.method).toEqual('GET');

      req.flush({});
    });
  });

  describe('assignTask', () => {
    it('should assign the task to a specific assignee', () => {
      const id = '1234';
      const assignee = 'demo';

      service
        .assignTask(id, assignee)
        .subscribe((data) => expect(data).toBeNull());

      const req = httpTestingController.expectOne(
        `/curo-api/tasks/${id}/assignee`
      );
      expect(req.request.method).toEqual('PUT');
      expect(req.request.body).toEqual({ assignee });

      req.flush(null);
    });
  });

  describe('completeTask', () => {
    it('should complete the task with variables', () => {
      const id = '1234';
      const variables = { firstname: 'Demo' };

      service
        .completeTask(id, variables, {
          flowToNext: true,
          returnVariables: true
        })
        .subscribe((data) => expect(data).toEqual({}));

      const req = httpTestingController.expectOne(
        `/curo-api/tasks/${id}/status?flowToNext=true&returnVariables=true`
      );
      expect(req.request.method).toEqual('POST');
      expect(req.request.body).toEqual(variables);

      req.flush({});
    });
  });
});
