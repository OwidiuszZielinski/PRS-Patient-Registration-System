# PayU Payment Integration

This document describes the PayU payment integration implemented in the PRS Patient Registration System.

## Overview

The system now supports PayU payment processing for medical visits that include additional services. When a patient registers a visit with services that have a cost, they will be redirected to PayU's payment gateway to complete the transaction.

## Features

- **Automatic Payment Detection**: The system automatically detects when payment is required based on selected services
- **PayU Sandbox Integration**: Uses PayU's sandbox environment for testing
- **Payment Status Tracking**: Tracks payment status and updates visit records accordingly
- **User-Friendly Flow**: Seamless user experience with clear status messages and redirects

## Configuration

### Backend Configuration

The PayU configuration is stored in `src/main/resources/application.properties`:

```properties
# PayU Configuration (Sandbox)
payu.merchant.id=145227
payu.pos.id=145227
payu.client.id=300746
payu.client.secret=2ee86a66e5d97e3fadc400c9f19b065d
payu.api.url=https://secure.snd.payu.com
payu.notify.url=http://localhost:8080/api/payment/callback
```

### Frontend Configuration

The frontend is configured to work with the backend on `http://localhost:8080` and runs on `http://localhost:3000`.

## Payment Flow

1. **Visit Registration**: Patient fills out visit form and selects additional services
2. **Payment Detection**: System checks if total cost > 0
3. **Payment Creation**: If payment needed, creates PayU order
4. **Redirect to PayU**: User is redirected to PayU payment page
5. **Payment Processing**: User completes payment on PayU
6. **Callback Handling**: PayU sends status back to our system
7. **Status Update**: System updates payment and visit status
8. **User Redirect**: User is redirected back to visit list or error page

## API Endpoints

### Payment Controller (`/api/payment`)

- `POST /create` - Create a new payment
- `POST /callback` - Handle PayU payment callbacks
- `GET /status/{paymentId}` - Get payment status

### Visit Controller (`/api/visit`)

- `POST /with-payment` - Register visit with payment processing

## Database Schema

### Payment Entity

```sql
CREATE TABLE payments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    payment_id VARCHAR(255),
    visit_id VARCHAR(255),
    patient_name VARCHAR(255),
    patient_email VARCHAR(255),
    amount DECIMAL(10,2),
    currency VARCHAR(3),
    status VARCHAR(50),
    description TEXT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);
```

## Frontend Components

### PatientVisitPanel.vue

- Enhanced with payment processing logic
- Shows loading states during payment
- Handles payment status checking
- Redirects to PayU when needed

### PaymentStatus.vue

- Displays payment success/failure status
- Auto-redirects after successful payment
- Provides manual navigation options

### PaymentService.js

- Handles payment API calls
- Manages payment status checking
- Integrates with visit registration

## Testing

### PayU Sandbox

The system uses PayU's sandbox environment for testing. Test card numbers:

- **Success**: 4111111111111111
- **Failure**: 4111111111111112
- **Expiry**: Any future date
- **CVV**: Any 3 digits

### Test Scenarios

1. **Visit without services** - Should register without payment
2. **Visit with services** - Should redirect to PayU
3. **Successful payment** - Should complete visit registration
4. **Failed payment** - Should show error and allow retry

## Error Handling

- **Payment Creation Failure**: Shows error message, stays on form
- **Payment Processing Failure**: Redirects to failure page
- **Network Issues**: Retry mechanism with user feedback
- **Invalid Payment Data**: Validation and error messages

## Security Considerations

- All payment data is transmitted over HTTPS
- Sensitive data is not stored in frontend
- Payment tokens are handled securely
- Callback verification prevents tampering

## Deployment Notes

### Production Configuration

For production deployment, update the configuration:

1. Change PayU URLs from sandbox to production
2. Update merchant credentials
3. Configure proper callback URLs
4. Set up SSL certificates
5. Update frontend URLs

### Environment Variables

Consider using environment variables for sensitive data:

```properties
payu.client.secret=${PAYU_CLIENT_SECRET}
payu.merchant.id=${PAYU_MERCHANT_ID}
```

## Troubleshooting

### Common Issues

1. **Payment not redirecting**: Check PayU credentials and API URLs
2. **Callback not working**: Verify callback URL configuration
3. **Payment status not updating**: Check database connection and callback handling
4. **Frontend errors**: Verify API endpoints and CORS configuration

### Logs

Check application logs for:
- Payment creation attempts
- PayU API responses
- Callback processing
- Database errors

## Support

For PayU integration issues:
1. Check PayU documentation
2. Verify sandbox credentials
3. Test with PayU test cards
4. Review application logs
5. Contact PayU support if needed 