export default function authHeader() {
  let user = JSON.parse(localStorage.getItem('user'));

  if (user && user.accessToken) {
    return { Authorization: 'Bearer ' + user.accessToken };
  } else {
    return {};
  }
}

// TODO ngrok utilizar quando utilizar o ngrok
// export default function authHeader() {
//   let user = JSON.parse(localStorage.getItem('user'));
//
//
//   if (user && user.accessToken) {
//     return { Authorization: 'Bearer ' + user.accessToken, 'ngrok-skip-browser-warning': 'true' };
//   } else {
//     return { 'ngrok-skip-browser-warning': 'true' };
//   }
// }
