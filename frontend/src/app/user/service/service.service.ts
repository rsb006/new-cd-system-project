import { HttpClient } from '@angular/common/http';
import { Inject, Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { MainService } from 'src/app/main.service';
import { ResponseModel } from 'src/assets/model/ResponseModel';
import { User } from '../model/user';
import { Userr } from '../model/userr';

@Injectable({
  providedIn: 'root',
})
export class ServiceService {

  //id:string="6324e2a86879610ec04cdc37";

  id:string;

  private getUserByEmailApi: string;
  private getUserbyIdApi: string;
  private updateApi: string;
  private getAllUsersApi: string;
  private deleteUserByIdApi:string;
  private markUserStatusApi :string;


  public user: User = new User();

  private AUTH_MS = 'http://localhost:8081/validate-token'

  constructor(private http: HttpClient) {
    
    (this.getUserByEmailApi = 'http://localhost:8082/user/get-user-by-email/'),

      (this.getUserbyIdApi ='http://localhost:8082/user/get-user/'),

      (this.updateApi = 'http://localhost:8082/user/update-user/'),

      (this.getAllUsersApi='http://localhost:8082/user/get-all-users'),

      (this.deleteUserByIdApi = 'http://localhost:8082/user/delete-user/'),

      (this.markUserStatusApi = 'http://localhost:8082/user/update-user-status/')

  }

    setId(userId:string):void{
        this.id=userId;
    }

    getId():string{
      return this.id;
    }

  getUser(): Observable<ResponseModel> {
    let req = new ResponseModel();
    let jwt: string = localStorage.getItem("jwt") || '';
    
    req.jwt = jwt;
      // return this.http.post<ResponseModel>(this.AUTH_MS, req);
    req.name = "xyz abc";
    return of(req);
    
 
  }

  public getUserByEmail(email: string): Observable<User> {
    return this.http.get<User>(this.getUserByEmailApi + email);
  }

  public getUserById(id: String): Observable<User> {
    return this.http.get<User>(this.getUserbyIdApi + id);
  }

  public updateUser(id: string, user: User): Observable<User> {
    return this.http.put<User>(this.updateApi + id, user);
  }

  public getAllUsers(): Observable<Userr[]> {
    return this.http.get<Userr[]>(this.getAllUsersApi);
  }


  // services for admin

  public markUserStatus(id:string, status:boolean ):Observable<any>{
     return this.http.patch(this.markUserStatusApi+id,status);
  }


   public deleteUserById(id:string):Observable<Object>{
       return this.http.delete(this.deleteUserByIdApi+id);
   }

}
