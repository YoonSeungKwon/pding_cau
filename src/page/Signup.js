import React, { useState } from 'react'
import axios from 'axios';
import { useNavigate } from "react-router-dom"

const Signup = () => {
  const [email, setEmail] = useState("");
  const [name, setName] = useState("");
  const [password, setPassword] = useState("");
  const [passwordCheck, setPasswordCheck] = useState('');
  
  const navigate = useNavigate();

  const handleNameChange = (e) => {
    setName(e.target.value);
  };

  const handleEmailChange = (e) => {
    setEmail(e.target.value);
  };

  const handlePasswordChange = (e) => {
    setPassword(e.target.value);
  };

  const handlePasswordCheckChange = (e) => {
    setPasswordCheck(e.target.value);
  };


  const register = () => {
    if (name && email && password && password === passwordCheck) {
      axios
    .post('http://13.209.154.183:8080/api/v1/members/', {
      name: name,
      email: email,
      password: password,
    })
    .then((response) => {
      // Handle success.
      console.log(response)
      console.log('Well done!');
      console.log('User profile', response.data);
      navigate("/login");
    })
    .catch((error) => {
      // Handle error.
      
      console.log('An error occurred:', error.response.data);
      const msg = error.response.data.message
      const code = error.response.data.status

      alert(msg + "  [" + code + "]")
    });
    }
  };
  
  return (
    <div>
        <div>
          <h3 id='signup_title'> 회원가입 (Signup) </h3>
        </div>
        <div className='Signup'>
          <div>
            <div id='signup_section'>
                {/* 이름 */}
                <div>
                <h5> 이름 </h5>
                <input type='text' maxLength='10' name='signup_name' onChange={handleNameChange}/>
                </div>
            </div>
            {/* 이메일 */}
            <div>
              <h5> 이메일 </h5>
              <input type='text' maxLength='15' name='signup_email' onChange={handleEmailChange}/> @
              <select name='signup_email_select'>
                <option value='gmail.com'> gmail.com </option>
                <option value='naver.com'> naver.com </option>
                <option value='write'> 직접 입력 </option>
              </select>
            </div>

            {/* 비밀번호 */}
            <div>
              <h5> 비밀번호 </h5>
              <input type='password' maxLength='15' name='signup_password' onChange={handlePasswordChange}/>
            </div>
            <div>
              <h5> 비밀번호 확인 </h5>
              <input type='password' maxLength='15' name='signup_pswCheck' onChange={handlePasswordCheckChange}/>
            </div>
          </div>
        </div>

        <div>
          <input type='button' value='가입하기' name='sigunup_submit'onClick={register} disabled={!(name && email && password && password === passwordCheck)}/>
        </div>
      </div>
  )
}

export default Signup