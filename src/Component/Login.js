import React, {useEffect, useState} from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import KakaoLogin from "react-kakao-login";

const Login = () =>{

    const [key, setKey] = useState("")

    useEffect(()=>{
        axios.get('http://localhost:8080/login/oauth2/code/kakao'
        ).then((res)=>{
            console.log(res)
            setKey(res.data.key)
        }).catch((error)=>{
            console.log(error)
        })
    },[])

    const navigate = new useNavigate();

    const [state, setState] = useState({
        email:"",
        password:"",
    })

    const handleChange = (e) =>{
        const {id , value} = e.target
        setState(prevState => ({
            ...prevState,
            [id] : value
        }))
    }

    const loginHandle = (e) =>{

        console.log(state)

        axios.post('http://localhost:8080/api/v1/members/login', state
        ).then((res)=>{
            console.log(res)
        }).catch((error)=>{
            console.log(error)
            alert("이메일 또는 비밀번호가 일치하지 않습니다.")
        })
    }

    const regiHandle = (e) =>{
        navigate("/api/v1/join")
    }

    const kakaoOnSuccess = (res) => {
        console.log(res)
        const token = res.response.access_token
        axios.post('http://localhost:8080/login/oauth2/code/kakao', token
        ).then((res)=>{
            console.log(res)
        }).catch((error)=>{
            console.log(error)
        })
    }

    const kakaoOnFailure = (error) =>{
        console.log(error)
    }

    return(
        <>
            Email   : <input type="text" id="email" onChange={handleChange}></input><br/>
            Password: <input type="password" id="password" onChange={handleChange}></input><br/>
            <button onClick={loginHandle}>로그인</button>
            <button onClick={regiHandle}>회원가입</button>
            <KakaoLogin
              token={key}
              onSuccess={kakaoOnSuccess}
              onFail={kakaoOnFailure}
            />
        </>
    )   

}
export default Login;