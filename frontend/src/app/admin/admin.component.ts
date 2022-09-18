import { Binary } from '@angular/compiler';
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { User } from '../user/model/user';
import { Userr } from '../user/model/userr';
import { ServiceService } from '../user/service/service.service';

@Component({
  selector: 'app-admin',
  templateUrl: './admin.component.html',
  styleUrls: ['./admin.component.css']
})
export class AdminComponent implements OnInit {

  users:Userr[];
  userId:string=''  ;
  userStatus:boolean;

  constructor(private userService:ServiceService,private router: Router) {
    userService.getAllUsers().subscribe(data=>this.users=data);
    console.log(this.users);
   }

  ngOnInit(): void {

    
  }



  updateUser(id:string):void{
   console.log("email in admin" + id);

    this.userService.setId(id)
    this.userId=id;
    console.log(this.userId)
    this.router.navigate(['admin/user/update']);

    
  }

  markUser(id:string,userStatus:boolean){

    this.userService.markUserStatus(id,userStatus).subscribe(data=>{
        console.log(data);
       })
    
       window.location.reload();

    
  }


 
  deleteUser(id:string){
    this.userService.deleteUserById(id).subscribe(data=>{
      console.log("deleted user response : "+data);
    })

  }



}
