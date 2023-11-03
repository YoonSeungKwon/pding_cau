import React from 'react'
import KakaoLogin from "react-kakao-login";
import axios from 'axios';
import { useState, useEffect } from 'react';


const KakaoLoginPage = () => {

    // const Rest_api_key = ''
    // const redirect_uri = ''
    // const kakaoURL = `https://kauth.kakao.com/oauth/authorize?client_id=${Rest_api_key}&redirect_uri=${redirect_uri}&response_type=code`
    // const handleLogin = ()=>{
    //     window.location.href = kakaoURL
    // }

    const [key, setKey] = useState("")

    useEffect(()=>{
      axios.get('http://13.209.154.183:8080/login/oauth2/code/kakao'
      ).then((res)=>{
          console.log(res)
          setKey(res.data.key)
      }).catch((error)=>{
          console.log(error)
      })
    },[])

    const kakaoOnSuccess = (res) => {
      console.log(res)
      const token = res.response.access_token
      axios.post('http://13.209.154.183:8080/login/oauth2/code/kakao', token//백엔드 서버로 토큰 보내기
      ).then((res)=>{
          console.log(res.data.email)
      }).catch((error)=>{
          console.log(error)
      })
  }

    const kakaoOnFailure = (error) =>{
      console.log(error)
    }

    return (
      <KakaoLogin
                token={key}   //api키 
                onSuccess={kakaoOnSuccess}
                onFail={kakaoOnFailure}
      />
    )
}

export default KakaoLoginPage