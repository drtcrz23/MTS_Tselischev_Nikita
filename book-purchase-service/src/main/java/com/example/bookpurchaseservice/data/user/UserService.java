package com.example.bookpurchaseservice.data.user;

import com.example.bookpurchaseservice.data.exceptions.InsufficientFundsException;
import com.example.bookpurchaseservice.data.exceptions.InvalidDataException;
import com.example.bookpurchaseservice.data.exceptions.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
@Service
public class UserService {
  private final UserRepository userRepository;

  @Autowired
  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Transactional
  public User createUser(String name, int balance) throws InvalidDataException {
    if (name == null) throw new InvalidDataException();
    return userRepository.save(new User(name, balance));
  }

  @Transactional
  public void payBook(Long userId, int amount) throws UserNotFoundException, InsufficientFundsException {
    User user = userRepository.findById(userId).orElse(null);
    if (user == null) throw new UserNotFoundException(userId);

    int balance = user.getBalance();

    if (balance - amount >= 0) {
      user.setBalance(balance - amount);
    } else {
      throw new InsufficientFundsException();
    }

    userRepository.save(user);
  }
  public User findUserById(Long userId) throws UserNotFoundException {
    User user = userRepository.findById(userId).orElse(null);
    if (user == null) throw new UserNotFoundException(userId);
    return user;
  }

  @Transactional(propagation = Propagation.REQUIRED)
  public void updateUser(Long userId, String name, int balance) throws UserNotFoundException {
    User user = userRepository.findById(userId).orElse(null);
    if (user == null) throw new UserNotFoundException(userId);
    user.setName(name);
    user.setBalance(balance);
    userRepository.save(user);
  }

  @Transactional(propagation = Propagation.REQUIRED)
  public void deleteUser(Long userId) throws UserNotFoundException{
    var user = userRepository.findById(userId).orElse(null);
    if (user == null) throw new UserNotFoundException(userId);
    userRepository.deleteById(userId);
  }
}
