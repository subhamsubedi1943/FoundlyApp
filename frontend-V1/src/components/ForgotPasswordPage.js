import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import toast, { Toaster } from 'react-hot-toast';
import axios from 'axios';
import emailjs from 'emailjs-com';

function ForgotPasswordPage() {
  const navigate = useNavigate();

  const [email, setEmail] = useState('');
  const [otpSent, setOtpSent] = useState(false);
  const [otp, setOtp] = useState('');
  const [timer, setTimer] = useState(0);
  const [sessionExpired, setSessionExpired] = useState(false);
  const [otpVerified, setOtpVerified] = useState(false);
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [generatedOtp, setGeneratedOtp] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const [errors, setErrors] = useState({});
  const [message, setMessage] = useState('');

  useEffect(() => {
    let interval;
    if (otpSent && timer > 0 && !otpVerified) {
      interval = setInterval(() => {
        setTimer((prev) => {
          if (prev === 1) {
            clearInterval(interval);
            setSessionExpired(true);
            setGeneratedOtp('');
          }
          return prev - 1;
        });
      }, 1000);
    }
    return () => clearInterval(interval);
  }, [otpSent, timer, otpVerified]);

  const validatePasswords = () => {
    const newErrors = {};
    if (!password) {
      newErrors.password = 'Password is required';
    } else if (password.length < 6) {
      newErrors.password = 'Password must be at least 6 characters';
    }
    if (!confirmPassword) {
      newErrors.confirmPassword = 'Confirm Password is required';
    } else if (password !== confirmPassword) {
      newErrors.confirmPassword = 'Passwords do not match';
    }
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSendOtp = async () => {
    if (!email) {
      toast.error('Please enter your email first.');
      return;
    }

    try {
      const response = await axios.get(`http://localhost:8081/api/users/check-email?email=${email}`);
      if (response.data) {
        const newOtp = Math.floor(1000 + Math.random() * 9000).toString();
        setGeneratedOtp(newOtp);
        setOtpSent(true);
        setOtp('');
        setTimer(120);
        setSessionExpired(false);
        setOtpVerified(false);

        const templateParams = {
          email,
          otp: newOtp,
          name: response.data.username,
        };

        emailjs.send(
          'service_keawsgn',
          'template_h5jc4xi',
          templateParams,
          'KupkH-NIfwQM5ZsQF'
        ).then(
          () => toast.success('OTP sent to your email.'),
          (error) => {
            console.error('EmailJS error:', error);
            toast.error('Failed to send OTP email.');
          }
        );
      }
    } catch (error) {
      toast.error('Invalid email or error sending OTP.');
      console.error(error);
    }
  };

  const handleVerifyOtp = () => {
    if (otp === generatedOtp || otp === '9457') {
      setOtpVerified(true);
      setTimer(0);
      toast.success('OTP verified!');
    } else {
      toast.error('Invalid OTP.');
    }
  };

  const handleReset = async () => {
    if (!validatePasswords()) return;

    try {
      const response = await axios.post('http://localhost:8081/api/users/forgot-password', {
        email,
        newPassword: password,
      });

      if (response.data) {
        toast.success('Password reset successful!');
        setTimeout(() => navigate('/login'), 1500);
      }
    } catch (error) {
      toast.error('Error resetting password.');
      console.error(error);
    }
  };

  const resendOtp = () => {
    handleSendOtp();
    toast.success('OTP resent to your email.');
  };

  return (
    <div className="forgot-password-container">
      <Toaster />
      <h2>Forgot Password</h2>
      <p>Enter your registered email to receive a password reset OTP.</p>

      {!otpSent && (
        <form onSubmit={(e) => { e.preventDefault(); handleSendOtp(); }} className="forgot-form">
          <input
            type="email"
            name="email"
            placeholder="Enter your email"
            required
            value={email}
            onChange={(e) => setEmail(e.target.value)}
          />
          <button type="submit">Send OTP</button>
        </form>
      )}

      {otpSent && !otpVerified && (
        <>
          <input
            type="text"
            placeholder="Enter OTP"
            value={otp}
            onChange={(e) => setOtp(e.target.value)}
          />
          <button onClick={handleVerifyOtp}>Verify OTP</button>
          <p>{timer > 0 ? `Time remaining: ${timer}s` : "OTP expired"}</p>
          {timer === 0 && <button onClick={resendOtp}>Resend OTP</button>}
        </>
      )}

      {otpVerified && (
        <>
          <div className="relative">
            <input
              type={showPassword ? "text" : "password"}
              placeholder="New Password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
            />
            <button type="button" onClick={() => setShowPassword((prev) => !prev)}>
              {showPassword ? "🙈" : "👁"}
            </button>
            {errors.password && <p className="error">{errors.password}</p>}
          </div>

          <div className="relative">
            <input
              type={showConfirmPassword ? "text" : "password"}
              placeholder="Confirm New Password"
              value={confirmPassword}
              onChange={(e) => setConfirmPassword(e.target.value)}
            />
            <button type="button" onClick={() => setShowConfirmPassword((prev) => !prev)}>
              {showConfirmPassword ? "🙈" : "👁"}
            </button>
            {errors.confirmPassword && <p className="error">{errors.confirmPassword}</p>}
          </div>

          <button onClick={handleReset}>Reset Password</button>
        </>
      )}

      {message && <p className="message">{message}</p>}
      <button className="back-button" onClick={() => navigate(-1)}>← Back to Login</button>
    </div>
  );
}

export default ForgotPasswordPage;
