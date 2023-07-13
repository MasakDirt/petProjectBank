# My Bank

This is my simple banking example to introduce you to my acquired skills and abilities. This project 
is based on Rest Api and Spring framework using JWT token for authentication, and it is about a bank where 
user can create his account where he can have multiple cards, with accounts and manage them.
For example: delete, add cards, receive and send funds, see transaction history, see your balance and so on.

## Table of Contents

- [Installation](#installation)
- [Usage](#usage)
- [About "Users"](#users)
- [About "Cards and Accounts"](#cards-and-accounts)
- [About "Transactions"](#transactions)
- [About "Authorization"](#authorization)
- [About "Security Config"](#security-config)
- [Conclusion](#conclusion)

## Installation

In my project, as you see, I have already committed pom.xml files and app.properties, 
so you only need to clone my repository to your PC and create in your mySql db table with name: 
"myBank" and write in app.properties your username and password.

$ git clone <repository-url>

## Usage

About using, you already had three user which created in db table when you start running app:
![img.png](photos/users-table.png)

Well, and all they need, you have too, example:
we have customer "Mike" who has role "ADMIN", so he has more opportunities, but not about it now.
So, you can authorize with Mike`s email and password, than you are provided with jwt token and go to all URLs(for which you are authorized) with that token.
Then you can check his: user-account, update account user or users(depends on userRole),
delete your account or if you ADMIN not only yours account, check your cards, create new card, replenish the balance, delete card,
create transactions = send someone else your money, for example: your mom need 150$,  you create a transaction in URL:
"/api/customers/{owner-id}/cards/{card-id}/transactions" with post mapping where must write your mom card and sum which
you want to transfer.

Next, let's analyze the main entities separately.

## "Users":
Users has his Roles table. Roles table need to distribute responsibilities before users and adding authorities.
In role table we have only Roles: "USER" and "ADMIN", but this does not exclude the fact that new ones may be added.
So, Admin can do almost all, but User only with what is his, or cannot interact at all, it depends on methods.
Customers as a human have fields: first_name, last_name, email, password, role and list of hisCards.
[Here you can check his methods and fields](src%2Fmain%2Fjava%2Fcom%2Fpet%2Fproject%2Fmodel%2Fentity%2FCustomer.java)

And that: [his Service class](src%2Fmain%2Fjava%2Fcom%2Fpet%2Fproject%2Fservice%2Fimpl%2FCustomerServiceImpl.java) where you can find 
[CardRepository class](src%2Fmain%2Fjava%2Fcom%2Fpet%2Fproject%2Frepository%2FCardRepository.java) for interactions with DB,
[his Controller class](src%2Fmain%2Fjava%2Fcom%2Fpet%2Fproject%2Fcontroller%2FCustomerController.java) where you can find all REST methods for Customer,
[customers DTO and mapper](src%2Fmain%2Fjava%2Fcom%2Fpet%2Fproject%2Fmodel%2Fdto%2Fcustomer) it`s for better interactions with REST usages.
The "Cards" field is also available to the user, so read on for more information about them:...

## "Cards and Accounts":
On my opinion, "Cards" it`s a middle strings between customer and bank administrators. 
So I was created a cards with random numbers, but only first four was constant (7835).
Card has an objects of his Owner and Account. [Card entity.](src%2Fmain%2Fjava%2Fcom%2Fpet%2Fproject%2Fmodel%2Fentity%2FCard.java)
In REST [CardController](src%2Fmain%2Fjava%2Fcom%2Fpet%2Fproject%2Fcontroller%2FCardController.java) I have methods for creating it, 
checking it, updating it, that is replenished the balance in account and of course deleting it is all has an PreAuthorize annotation to
preventing you from doing something bad with the card. Here you can check [CardRepository,](src%2Fmain%2Fjava%2Fcom%2Fpet%2Fproject%2Frepository%2FCardRepository.java)
and [CardServiceImpl.java](src%2Fmain%2Fjava%2Fcom%2Fpet%2Fproject%2Fservice%2Fimpl%2FCardServiceImpl.java) for understanding constructions of my project.
[Cards DTO and mapper](src%2Fmain%2Fjava%2Fcom%2Fpet%2Fproject%2Fmodel%2Fdto%2Fcard)

I think account is the main in card. We go to the [account entity,](src%2Fmain%2Fjava%2Fcom%2Fpet%2Fproject%2Fmodel%2Fentity%2FAccount.java) now.
In account, I have: balance, card(between card and account I have created one-to-one relationship), list of transactions (which I will write about a little later).
In my [CardController](src%2Fmain%2Fjava%2Fcom%2Fpet%2Fproject%2Fcontroller%2FCardController.java) if you can see, are even more closely connected account and card
I consider it necessary, because it is no longer right to separate them too much, and they are like twin brothers who will not last long without each other.
Since account and card are connect, so they have identical methods in REST controller.

Lat`s talk about transactions...

## "Transactions":

Transactions it`s the most interesting and unpredictable entity for whole project. I liked that! 
So in [Transaction entity](src%2Fmain%2Fjava%2Fcom%2Fpet%2Fproject%2Fmodel%2Fentity%2FTransaction.java) you can see much more fields than in previous entities.
Transaction fields: createdAt - it is time when transaction was happened, transferAmount - it is sum which user wants to transfer to another user using the card
for it, I created field - recipientCard, than we have balanceAfter- balance which was left after transaction and fundsWithdrawn - 
funds which money that was spent from the card(account).
I like my transactions classes, and hope that you will like it ,too. 

Here you can see [Transactions DTO and mapper](src%2Fmain%2Fjava%2Fcom%2Fpet%2Fproject%2Fmodel%2Fdto%2Ftransaction) last created using mapstruct library.
[Transaction Controller](src%2Fmain%2Fjava%2Fcom%2Fpet%2Fproject%2Fcontroller%2FTransactionController.java) class with my REST methods for transactions and here you can see using
[TransactionServiceImpl class](src%2Fmain%2Fjava%2Fcom%2Fpet%2Fproject%2Fservice%2Fimpl%2FTransactionServiceImpl.java) in which using [TransactionRepository](src%2Fmain%2Fjava%2Fcom%2Fpet%2Fproject%2Frepository%2FTransactionRepository.java), 
so you can check it.
In REST [Transaction class](src%2Fmain%2Fjava%2Fcom%2Fpet%2Fproject%2Fcontroller%2FTransactionController.java) I using get mapping for gets all transactions, 
something like history and one transaction, can create it, I talked about this in the example at the beginning and delete it, but it can do only Admin.

## "Authorization":

As I said, I am using JWT token in my SecurityContext for Authorization, [here my auth methods](src%2Fmain%2Fjava%2Fcom%2Fpet%2Fproject%2Fcontroller%2FAuthController.java)
where I used [JwtUtils for generating token](src%2Fmain%2Fjava%2Fcom%2Fpet%2Fproject%2Futils%2FJwtUtils.java), 
[from AppConfig passwordEncoder](src%2Fmain%2Fjava%2Fcom%2Fpet%2Fproject%2Fconfig%2FAppConfig.java) to encode all customers passwords
and Services, mapper to creating a customer.

So I also used [AuthTokenFilter](src%2Fmain%2Fjava%2Fcom%2Fpet%2Fproject%2Ffilter%2FAuthTokenFilter.java) for filtering tokens, 
[GlobalExceptionHandler class](src%2Fmain%2Fjava%2Fcom%2Fpet%2Fproject%2Fexception%2FGlobalExceptionHandler.java) which catch all exceptions and
gives user understanding information for him.

## "Security Config":
In [Security Config](src%2Fmain%2Fjava%2Fcom%2Fpet%2Fproject%2Fconfig%2FSecurityConfig.java) I had 
[AuthEntryPointJwt](src%2Fmain%2Fjava%2Fcom%2Fpet%2Fproject%2Fcomponent%2FAuthEntryPointJwt.java) for sending errors
if something goes wrong during authorization,
and I had three URLs which allows without authorization:
it is home page, login and register.

## "Conclusion":

I really enjoyed developing my project, and I want to try myself on a real project as soon as possible.
Thank you for paying attention to my project.

I hope this was clear to you, and if not, you can contact me for further details:
/Telegram: `@mskdrttt`/
E-mail: `maksimkarulet8@gmail.com`.