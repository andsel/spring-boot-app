import React from 'react';
import ReactDOM from 'react-dom';
import axios from 'axios';

class Message extends React.Component {
  constructor(props) {
    super(props);
   }

  render() {
    const message = this.props.message
    return (
      <tr>
        <td>{message}</td>
      </tr>);
  }
}

class MessageList extends React.Component {
  constructor(props) {
    super(props);
    this.state = {messages: []};
  }

  componentDidMount() {
    axios.get('/devices/API/messages/' + this.props.clientid)
      .then((response) => this.receivedMessagesList(response));
  }

  receivedMessagesList(response) {
    console.log(response);
    const loadedMessages = response.data;
    this.setState({messages: loadedMessages});
  }

  render() {
    const listItems = this.state.messages.map((message) =>
        <Message message={message}/>
    );
    return (
        <table class="table table-striped">
            <thead>
            <tr>
                <th scope="col">Messages received</th>
            </tr>
            </thead>
            <tbody>{listItems}</tbody>
        </table>);
  }
}

var root = document.getElementById('react');
ReactDOM.render(
  <MessageList {...(root.dataset)}/>,
//<label> ciaone </label>,
  root
);