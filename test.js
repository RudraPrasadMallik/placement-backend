fetch('http://localhost:8080/api/students/register', {
    method: 'POST',
    headers: { 'Content-Type': 'multipart/form-data; boundary=---boundary' },
    body: '---boundary\r\nContent-Disposition: form-data; name="fullName"\r\n\r\nTest\r\n---boundary--\r\n'
}).then(res => res.text()).then(console.log).catch(console.error);
