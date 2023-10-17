import React, {useState} from "react";
import axios from "axios";

const Register = () =>{

    const [state, setState] = useState({
        email:"",
        password:"",
        name:""
    })

    const handleChange = (e) =>{
        const {id , value} = e.target
        setState(prevState => ({
            ...prevState,
            [id] : value
        }))
    }

    const submitHandle = (e) =>{

        console.log(state)

        axios.post('http://localhost:8080/api/v1/members/', state
        ).then((res)=>{
            console.log(res)
        }).catch((error)=>{
            console.log(error)
        })
    }

    const kakaoHandle = (e) =>{
        
    }

    return(
        <>
            Email   : <input type="text" id="email" onChange={handleChange}></input><br/>
            Password: <input type="password" id="password" onChange={handleChange}></input><br/>
            Name    : <input type="text" id="name" onChange={handleChange}></input><br/>
            <button onClick={submitHandle}>회원가입</button>
            <button onClick={kakaoHandle}>카카오로 로그인</button>
        </>
    )   

}
export default Register;